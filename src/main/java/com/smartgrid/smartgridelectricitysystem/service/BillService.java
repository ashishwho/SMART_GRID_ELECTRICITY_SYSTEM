package com.smartgrid.smartgridelectricitysystem.service;

import com.smartgrid.smartgridelectricitysystem.exception.DuplicateResourceException;
import com.smartgrid.smartgridelectricitysystem.exception.InsufficientBalanceException;
import com.smartgrid.smartgridelectricitysystem.exception.InvalidCredentialsException;
import com.smartgrid.smartgridelectricitysystem.exception.ResourceNotFoundException;
import com.smartgrid.smartgridelectricitysystem.exception.ValidationException;
import com.smartgrid.smartgridelectricitysystem.model.*;
import com.smartgrid.smartgridelectricitysystem.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smartgrid.smartgridelectricitysystem.repository.WalletRepository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final CustomerRepository customerRepository;
    private final WalletService walletService;
    private final BankAccountRepository bankAccountRepository;
    private final WalletRepository walletRepository;
    private final SessionService sessionService;
    private final BankAccountService bankAccountService;

    public BillService(BillRepository billRepository,
                       CustomerRepository customerRepository,
                       WalletService walletService,
                       BankAccountRepository bankAccountRepository,
                       WalletRepository walletRepository,
                       SessionService sessionService,
                       BankAccountService bankAccountService) {
        this.billRepository = billRepository;
        this.customerRepository = customerRepository;
        this.walletService = walletService;
        this.bankAccountRepository = bankAccountRepository;
        this.walletRepository = walletRepository;
        this.sessionService=sessionService;
        this.bankAccountService=bankAccountService;
    }
    @Transactional
    public Bill createBill(
            String meterNo,
            int year,
            int month,
            double totalAmount) {

        sessionService.requireEmployee();

        if (totalAmount <= 0) {
            throw new ValidationException(
                    "Bill amount must be positive");
        }

        Customer customer =
                customerRepository.findById(meterNo)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found: "
                                                + meterNo));

        YearMonth requested =
                YearMonth.of(year, month);

        if (!requested.isBefore(YearMonth.now())) {
            throw new ValidationException(
                    "Bills can only be created for past months");
        }

        LocalDate billDate =
                requested.atEndOfMonth();

        boolean billExists =
                billRepository.findByCustomerMeterNo(meterNo).stream().anyMatch(
                        b->YearMonth.from(
                                b.getBillDate())
                                .equals(requested));

        if(billExists){
            throw new DuplicateResourceException("Bill already exits for customer "+meterNo + " for month "+ month+ " year "+ year);
        }

        Bill bill = new Bill(
                customer,
                billDate,
                totalAmount
        );

        return billRepository.save(bill);
    }

    public List<Bill> getPendingBills(String meterNo) {
        sessionService.requireCustomer(meterNo);

        customerRepository.findById(meterNo)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found: "
                                        + meterNo));

        return billRepository
                .findByCustomerMeterNoAndStatus(
                        meterNo,
                        BillStatus.PENDING);
    }

    public List<Bill> getPaidBills(String meterNo) {
        sessionService.requireCustomer(meterNo);
        return billRepository.findByCustomerMeterNoAndStatus(meterNo, BillStatus.PAID);
    }



    @Transactional
    public boolean payBill(
            String meterNo,
            Long billId,
            double walletAmount,
            String bankPassword) {

        sessionService.requireCustomer(meterNo);

        Bill bill = billRepository
                .findByBillIdAndStatus(
                        billId,
                        BillStatus.PENDING)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No such pending bill found"));

        if (!bill.getCustomer().getMeterNo().equals(meterNo)) {
            throw new ValidationException(
                    "You can only pay your own bills");
        }

        double totalAmount =
                bill.getTotalAmount();

        if (walletAmount < 0) {
            throw new ValidationException(
                    "Wallet amount cannot be negative");
        }

        if (walletAmount > totalAmount) {
            throw new ValidationException(
                    "Wallet amount cannot exceed bill amount");
        }

        double bankAmount =
                totalAmount - walletAmount;

        Wallet wallet =
                walletService.getWalletByMeterNo(
                        meterNo);

        if (wallet.getBalance() < walletAmount) {
            throw new InsufficientBalanceException(
                    "Insufficient wallet balance");
        }

        Customer customer =
                bill.getCustomer();



        if(bankAmount > 0) {

            BankAccount utility =
                    bankAccountService
                            .getUtilityBankAccount();

            BankAccount customerBank =
                    bankAccountRepository
                            .findById(
                                    customer.getLinkedBankAccNo())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Customer bank account not found"));

            if (!customerBank.getPassword()
                    .equals(bankPassword)) {

                throw new InvalidCredentialsException(
                        "Invalid bank account password");
            }


            if (customerBank.getBalance()
                    < bankAmount) {

                throw new InsufficientBalanceException(
                        "Insufficient bank balance");
            }

            customerBank.debit(bankAmount);
            bankAccountRepository.save(customerBank);

            utility.credit(bankAmount);
            bankAccountRepository.save(utility);
        }



        if (walletAmount > 0) {
            wallet.debit(walletAmount);
            walletRepository.save(wallet);
        }



        bill.setStatus(BillStatus.PAID);
        bill.setPaidFromWallet(walletAmount);
        bill.setPaidFromBank(bankAmount);

        billRepository.save(bill);

        //if no remaining bill, and conn was off, turn it on
        int remainingPending =
                billRepository
                        .countByCustomerMeterNoAndStatus(
                                meterNo,
                                BillStatus.PENDING);

        if (remainingPending == 0 && !customer.isConnectionStatus()) {
            customer.setConnectionStatus(true);
            customerRepository.save(customer);
        }

        return true;
    }

    public List<Bill> searchBills(
            int fromYear,
            int fromMonth,
            int toYear,
            int toMonth,
            BillStatus status) {

        sessionService.requireEmployee();


        if (fromMonth < 1 || fromMonth > 12) {
            throw new ValidationException(
                    "From month must be between 1 and 12");
        }

        if (toMonth < 1 || toMonth > 12) {
            throw new ValidationException(
                    "To month must be between 1 and 12");
        }

        LocalDate startDate =
                LocalDate.of(
                        fromYear,
                        fromMonth,
                        1);

        LocalDate endDate =
                LocalDate.of(
                                toYear,
                                toMonth,
                                1)
                        .withDayOfMonth(
                                LocalDate.of(
                                                toYear,
                                                toMonth,
                                                1)
                                        .lengthOfMonth());

        if (startDate.isAfter(endDate)) {
            throw new ValidationException(
                    "Start date cannot be after end date");
        }

        return billRepository
                .findByStatusAndBillDateBetween(
                        status,
                        startDate,
                        endDate);
    }


}