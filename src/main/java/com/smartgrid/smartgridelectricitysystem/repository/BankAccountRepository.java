package com.smartgrid.smartgridelectricitysystem.repository;

import com.smartgrid.smartgridelectricitysystem.model.AccountType;
import com.smartgrid.smartgridelectricitysystem.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
    Optional<BankAccount> findByAccNoAndPassword(String accNo, String password);

    Optional<BankAccount> findByType(
            AccountType type);
}



