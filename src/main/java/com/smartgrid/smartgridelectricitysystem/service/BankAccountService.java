package com.smartgrid.smartgridelectricitysystem.service;

import com.smartgrid.smartgridelectricitysystem.exception.*;
import com.smartgrid.smartgridelectricitysystem.model.*;
import com.smartgrid.smartgridelectricitysystem.repository.BankAccountRepository;
import com.smartgrid.smartgridelectricitysystem.repository.CustomerRepository;
import com.smartgrid.smartgridelectricitysystem.repository.UtilityAccountRepository;
import com.smartgrid.smartgridelectricitysystem.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final CustomerRepository customerRepository;
    private final WalletService walletService;
    private final SessionService sessionService;
    private final UtilityAccountRepository utilityAccountRepository;


    public BankAccountService(BankAccountRepository bankAccountRepository,
                              CustomerRepository customerRepository,
                              WalletService walletService,
                              SessionService sessionService,
                              UtilityAccountRepository utilityAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.customerRepository = customerRepository;
        this.walletService = walletService;
        this.sessionService = sessionService;
        this.utilityAccountRepository = utilityAccountRepository;
    }

    @Transactional
    public UtilityAccount addUtilityAccount(
            String bankAccNo,
            String utilityName) {

        sessionService.requireEmployee();

        BankAccount bankAccount =
                bankAccountRepository
                        .findById(bankAccNo)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Bank account not found"));

        if (utilityAccountRepository
                .existsById(bankAccNo)) {

            throw new DuplicateResourceException(
                    "Utility account already exists");
        }

        UtilityAccount utility =
                new UtilityAccount(
                        bankAccNo,
                        utilityName);

        return utilityAccountRepository
                .save(utility);
    }

    public BankAccount getUtilityBankAccount() {

        UtilityAccount utility =
                utilityAccountRepository
                        .findAll()
                        .stream()
                        .findFirst()
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No utility bank account found, error from system side"));

        return bankAccountRepository
                .findById(
                        utility.getBankAccNo())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No utility bank account found, error from system side"));
    }


    public BankAccount getBankAccount(String accNo) {
        return bankAccountRepository.findById(accNo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bank account not found: " + accNo));
    }




    // VIRTUAL → BANK CONVERSION

    @Transactional
    public boolean convertVirtualToBank(String meterNo, double amount, String bankPassword) {


        sessionService.requireCustomer(meterNo);
        // 1. Validate amount
        if (amount <= 0) {
            throw new ValidationException("Amount must be greater than zero");
        }

        // 2. Get customer
        Customer customer = customerRepository.findById(meterNo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found: " + meterNo));

        // 3. Get wallet and check balance
        Wallet wallet = walletService.getWalletByMeterNo(meterNo);
        if (wallet.getBalance() < amount) {
            throw new InsufficientBalanceException(
                    "Insufficient wallet balance. Available: " + wallet.getBalance()
                            + ", Required: " + amount);
        }

        // 4. Check bank account linked
        String accNo = customer.getLinkedBankAccNo();
        if (accNo == null) {
            throw new ValidationException(
                    "No bank account linked to customer: " + meterNo);
        }

        // 5. Get customer bank account
        BankAccount customerBank = getBankAccount(accNo);

        // 6. Verify bank password
        if (!customerBank.getPassword().equals(bankPassword)) {
            throw new InvalidCredentialsException("Invalid bank account password");
        }

        // 7. Get utility account and check balance
        BankAccount utility =
                getUtilityBankAccount();

        if (utility.getBalance() < amount) {
            throw new InsufficientBalanceException(
                    "Utility account has insufficient funds for conversion, error from system side");
        }

        // 8. Perform transfer
        walletService.debitWallet(meterNo, amount);   // wallet ↓
        utility.debit(amount);                         // utility ↓
        customerBank.credit(amount);                   // customer bank ↑

        bankAccountRepository.save(utility);
        bankAccountRepository.save(customerBank);

        return true;
    }
}
