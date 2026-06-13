package com.smartgrid.smartgridelectricitysystem.model;

import jakarta.persistence.*;

@Entity
@Table(name = "bank_accounts")
public class BankAccount {

    @Id
    @Column(name = "acc_no", nullable = false, unique = true)
    private String accNo;

    @Column(nullable = false)
    private String accountHolderName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private double balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    public BankAccount() {}

    public BankAccount(String accNo, String accountHolderName, String password, double balance, AccountType type) {
        this.accNo = accNo;
        this.accountHolderName = accountHolderName;
        this.password = password;
        this.balance = balance;
        this.type = type;
    }

    public String getAccNo() {
        return accNo;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getBalance() {
        return balance;
    }

    public AccountType getType() {
        return type;
    }

    public void credit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to be credited must be positive");
        }
        this.balance += amount;
    }

    public void debit(double amount) {
        if(amount <= 0){
            throw new IllegalArgumentException("Debit amount must be positive");

        }
        if(amount > balance){
            throw new IllegalArgumentException("Insufficient balance");
        }
        this.balance -= amount;
    }
}