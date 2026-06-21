package com.smartgrid.smartgridelectricitysystem.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "energy_records")
public class EnergyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "units_produced", nullable = false)
    private double unitsProduced;

    @Column(name = "rate_per_unit", nullable = false)
    private double ratePerUnit;

    @Column(name = "virtual_money", nullable = false)
    private double virtualMoney;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @ManyToOne
    @JoinColumn(name = "meter_no", nullable = false)
    private Customer customer;

    public EnergyRecord() {
    }

    public EnergyRecord(
            double unitsProduced,
            double ratePerUnit,
            LocalDate recordDate,
            Customer customer) {

        if (unitsProduced <= 0) {
            throw new IllegalArgumentException(
                    "Units produced must be positive");
        }

        if (ratePerUnit <= 0) {
            throw new IllegalArgumentException(
                    "Rate per unit must be positive");
        }

        if (recordDate == null) {
            throw new IllegalArgumentException(
                    "Record date cannot be null");
        }

        if (customer == null) {
            throw new IllegalArgumentException(
                    "Customer cannot be null");
        }

        this.unitsProduced = unitsProduced;
        this.ratePerUnit = ratePerUnit;

        // Always computed automatically
        this.virtualMoney = unitsProduced * ratePerUnit;

        this.recordDate = recordDate;
        this.customer = customer;
    }

    public Long getId() {
        return id;
    }

    public double getUnitsProduced() {
        return unitsProduced;
    }

    public double getRatePerUnit() {
        return ratePerUnit;
    }

    public double getVirtualMoney() {
        return virtualMoney;
    }

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public Customer getCustomer() {
        return customer;
    }
}