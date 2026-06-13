package com.smartgrid.smartgridelectricitysystem.service;

import com.smartgrid.smartgridelectricitysystem.exception.InsufficientBalanceException;
import com.smartgrid.smartgridelectricitysystem.exception.InvalidCredentialsException;
import com.smartgrid.smartgridelectricitysystem.exception.ResourceNotFoundException;
import com.smartgrid.smartgridelectricitysystem.exception.ValidationException;
import com.smartgrid.smartgridelectricitysystem.model.*;
import com.smartgrid.smartgridelectricitysystem.repository.BankAccountRepository;
import com.smartgrid.smartgridelectricitysystem.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final CustomerRepository customerRepository;
    private final WalletService walletService;
    private final SessionService sessionService;

    public BankAccountService(BankAccountRepository bankAccountRepository,
                              CustomerRepository customerRepository,
                              WalletService walletService,
                              SessionService sessionService) {
        this.bankAccountRepository = bankAccountRepository;
        this.customerRepository = customerRepository;
        this.walletService = walletService;
        this.sessionService = sessionService;
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
                bankAccountRepository
                        .findByType(AccountType.UTILITY)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Utility account not found"));

        if (utility.getBalance() < amount) {
            throw new InsufficientBalanceException(
                    "Utility account has insufficient funds for conversion");
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
