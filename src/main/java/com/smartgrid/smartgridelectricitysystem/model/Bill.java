package com.smartgrid.smartgridelectricitysystem.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billId;

    @ManyToOne
    @JoinColumn(name = "meter_no", nullable = false)
    private Customer customer;

    @Column(name = "bill_date", nullable = false)
    private LocalDate billDate;

    @Column(name = "total_amount", nullable = false)
    private double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillStatus status;

    @Column(name = "paid_from_wallet", nullable = false)
    private double paidFromWallet;

    @Column(name = "paid_from_bank", nullable = false)
    private double paidFromBank;

    public Bill() {}

    public Bill(Customer customer,
                LocalDate billDate,
                double totalAmount) {

        if (customer == null) {
            throw new IllegalArgumentException(
                    "Customer cannot be null");
        }

        if (billDate == null) {
            throw new IllegalArgumentException(
                    "Bill date cannot be null");
        }

        if (totalAmount <= 0) {
            throw new IllegalArgumentException(
                    "Total amount must be positive");
        }

        this.customer = customer;
        this.billDate = billDate;
        this.totalAmount = totalAmount;

        this.status = BillStatus.PENDING;
        this.paidFromWallet = 0.0;
        this.paidFromBank = 0.0;
    }

    public Long getBillId() {
        return billId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public LocalDate getBillDate() {
        return billDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public BillStatus getStatus() {
        return status;
    }

    public double getPaidFromWallet() {
        return paidFromWallet;
    }

    public double getPaidFromBank() {
        return paidFromBank;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public void setPaidFromWallet(double paidFromWallet) {
        this.paidFromWallet = paidFromWallet;
    }

    public void setPaidFromBank(double paidFromBank) {
        this.paidFromBank = paidFromBank;
    }
}