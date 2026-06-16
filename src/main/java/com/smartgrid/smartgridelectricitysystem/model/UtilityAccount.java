package com.smartgrid.smartgridelectricitysystem.model;

import jakarta.persistence.*;

@Entity
@Table(name = "utility_accounts")
public class UtilityAccount {

    @Id
    private String bankAccNo;

    @Column(nullable = false)
    private String utilityName;

    public UtilityAccount() {}

    public UtilityAccount(
            String bankAccNo,
            String utilityName) {

        this.bankAccNo = bankAccNo;
        this.utilityName = utilityName;
    }

    public String getBankAccNo() {
        return bankAccNo;
    }

    public String getUtilityName() {
        return utilityName;
    }
}