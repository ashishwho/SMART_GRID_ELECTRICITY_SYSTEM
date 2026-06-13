package com.smartgrid.smartgridelectricitysystem.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @Column(name = "meter_no", nullable = false, unique = true)
    private String meterNo;

    @Column(name="name",nullable = false)
    private String name;

    @Column(name="password", nullable = false,unique = true)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerType type;

    @Column(name = "connection_status")
    private boolean connectionStatus;

    @Column(name = "has_solar_panel")
    private boolean hasSolarPanel;

    @Column(name = "linked_bank_acc_no")
    private String linkedBankAccNo;

    public Customer() {
    }

    public Customer(String meterNo, String name, String password, CustomerType type,
                    boolean connectionStatus, boolean hasSolarPanel, String linkedBankAccNo) {
        this.meterNo = meterNo;
        this.name = name;
        this.password = password;
        this.type = type;
        this.connectionStatus = connectionStatus;
        this.hasSolarPanel = hasSolarPanel;
        this.linkedBankAccNo = linkedBankAccNo;
    }

    public String getMeterNo() {
        return meterNo;
    }

    public void setMeterNo(String meterNo) {
        this.meterNo = meterNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public CustomerType getType() {
        return type;
    }

    public void setType(CustomerType type) {
        this.type = type;
    }

    public boolean isConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(boolean connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public boolean isHasSolarPanel() {
        return hasSolarPanel;
    }

    public void setHasSolarPanel(boolean hasSolarPanel) {
        this.hasSolarPanel = hasSolarPanel;
    }

    public String getLinkedBankAccNo() {
        return linkedBankAccNo;
    }

    public void setLinkedBankAccNo(String linkedBankAccNo) {
        this.linkedBankAccNo = linkedBankAccNo;
    }
}