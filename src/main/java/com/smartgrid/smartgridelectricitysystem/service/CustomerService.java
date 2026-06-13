package com.smartgrid.smartgridelectricitysystem.service;

import com.smartgrid.smartgridelectricitysystem.exception.DuplicateResourceException;
import com.smartgrid.smartgridelectricitysystem.exception.ResourceNotFoundException;
import com.smartgrid.smartgridelectricitysystem.exception.ValidationException;
import com.smartgrid.smartgridelectricitysystem.model.*;
import com.smartgrid.smartgridelectricitysystem.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final BillRepository billRepository;
    private final BankAccountRepository bankAccountRepository;
    private final WalletRepository walletRepository;
    private final SessionService sessionService;
    private final WalletService walletService;

    public CustomerService(CustomerRepository customerRepository,
                           BillRepository billRepository,
                           BankAccountRepository bankAccountRepository,
                           WalletRepository walletRepository, SessionService sessionService, WalletService walletService) {
        this.customerRepository = customerRepository;
        this.billRepository = billRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.walletRepository = walletRepository;
        this.sessionService = sessionService;
        this.walletService = walletService;
    }

    public List<Customer> getAllCustomers() {
        sessionService.requireEmployee();
        return customerRepository.findAll();
    }

    public Customer getCustomerByMeterNo(String meterNo) {
        sessionService.requireEmployee();
        return customerRepository.findById(meterNo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found: " + meterNo));
    }

    @Transactional
    public Customer addCustomer(
            String meterNo,
            String name,
            String password,
            CustomerType type,
            boolean hasSolarPanel,
            String bankAccNo) {

        sessionService.requireEmployee();

        if (customerRepository.existsById(meterNo)) {
            throw new DuplicateResourceException(
                    "Customer with meter no "
                            + meterNo
                            + " already exists");
        }

        BankAccount bankAccount =
                bankAccountRepository.findById(bankAccNo)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Bank account "
                                                + bankAccNo
                                                + " not found. Customer cannot be added."
                                ));

        if (!bankAccount.getAccountHolderName()
                .equalsIgnoreCase(name)) {

            throw new ValidationException(
                    "Name does not match bank account records.");
        }

        Customer customer = new Customer(
                meterNo,
                name,
                password,
                type,
                true,
                hasSolarPanel,
                bankAccNo
        );

        Customer savedCustomer =
                customerRepository.save(customer);

        walletService.createWalletForCustomer(customer);

        System.out.println(
                "Customer added: "
                        + savedCustomer.getMeterNo()
                        + " | Wallet created | Bank linked: "
                        + bankAccNo);

        return savedCustomer;
    }

    @Transactional
    public boolean toggleConnection(String meterNo) {

        sessionService.requireEmployee();

        Customer customer = getCustomerByMeterNo(meterNo);
        boolean newStatus = !customer.isConnectionStatus();
        customer.setConnectionStatus(newStatus);
        customerRepository.save(customer);
        return newStatus;
    }

    @Transactional
    public int cutOverdueConnections() {

        sessionService.requireEmployee();

        LocalDate cutoffDate =
                LocalDate.now().minusMonths(3);

        List<Bill> pendingBills =
                billRepository
                        .findByStatusAndBillDateBefore(
                                BillStatus.PENDING,
                                cutoffDate
                        );

        int count = 0;

        for (Bill bill : pendingBills) {

            Customer customer =
                    bill.getCustomer();

            if (customer.isConnectionStatus()) {

                customer.setConnectionStatus(false);

                count++;
            }
        }

        return count;
    }


}