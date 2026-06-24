package com.smartgrid.smartgridelectricitysystem.service;

import com.smartgrid.smartgridelectricitysystem.exception.InsufficientBalanceException;
import com.smartgrid.smartgridelectricitysystem.exception.ResourceNotFoundException;
import com.smartgrid.smartgridelectricitysystem.exception.ValidationException;
import com.smartgrid.smartgridelectricitysystem.model.Customer;
import com.smartgrid.smartgridelectricitysystem.model.Wallet;
import com.smartgrid.smartgridelectricitysystem.repository.CustomerRepository;
import com.smartgrid.smartgridelectricitysystem.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final SessionService sessionService;

    public WalletService(WalletRepository walletRepository,SessionService sessionService) {
        this.walletRepository = walletRepository;
        this.sessionService = sessionService;

    }

    public Wallet createWalletForCustomer(Customer customer) {
        Wallet wallet = new Wallet(customer);
        return walletRepository.save(wallet);
    }

    public Wallet getWalletByMeterNo(String meterNo) {
        if("EMPLOYEE".equals(sessionService.getRole())){
            //emp can access any customer's wallet
        }
        else{
            sessionService.requireCustomer(meterNo);
        }
        return walletRepository.findByMeterNo(meterNo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Wallet not found for meter no: " + meterNo));
    }

    @Transactional
    public Wallet creditWallet(String meterNo, double amount) {
        if (amount <= 0) {
            throw new ValidationException(
                    "Credit amount must be greater than zero");
        }
        Wallet wallet = getWalletByMeterNo(meterNo);
        wallet.credit(amount);
        return walletRepository.save(wallet);
    }

    @Transactional
    public boolean debitWallet(String meterNo, double amount) {
        if (amount <= 0) {
            throw new ValidationException(
                    "Debit amount must be greater than zero");
        }
        Wallet wallet = getWalletByMeterNo(meterNo);
        if (wallet.getBalance() < amount) {
            throw new InsufficientBalanceException(
                    "Insufficient wallet balance. Available: " + wallet.getBalance()
                            + ", Required: " + amount);
        }
        wallet.debit(amount);
        walletRepository.save(wallet);
        return true;
    }

}