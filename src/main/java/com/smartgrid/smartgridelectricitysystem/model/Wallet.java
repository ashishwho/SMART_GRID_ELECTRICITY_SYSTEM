package com.smartgrid.smartgridelectricitysystem.model;

import jakarta.persistence.*;

@Entity
@Table(name = "wallets")
public class Wallet {


    @Id
    @Column(name = "meter_no")
    private String meterNo;

    @OneToOne
    @JoinColumn(name = "meter_no", referencedColumnName = "meter_no", insertable = false, updatable = false)
    private Customer customer;

    @Column(nullable = false)
    private double balance = 0.0;

    public Wallet() {}

    public Wallet(Customer customer) {
        this.meterNo = customer.getMeterNo();
        this.balance = 0.0;
    }

    public String getMeterNo() { return meterNo; }

    public Customer getCustomer() { return customer; }

    public double getBalance() { return balance; }

    public void credit(double amount) {
        if(amount<=0) throw new IllegalArgumentException("Credit amount must be positive");
        this.balance += amount;
    }

    public void debit(double amount) {
        if(amount <= 0) throw new IllegalArgumentException("Debit amount must be positive");
        if (amount > balance) throw new IllegalArgumentException("Insufficient balance");
        this.balance -= amount;
    }
}