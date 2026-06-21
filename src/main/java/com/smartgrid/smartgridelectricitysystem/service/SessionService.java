package com.smartgrid.smartgridelectricitysystem.service;

import org.springframework.stereotype.Service;
import com.smartgrid.smartgridelectricitysystem.exception.ValidationException;

@Service
public class SessionService {

    private String role;

    private String meterNo;

    private String employeeId;

    public void loginCustomer(String meterNo) {
        this.role = "CUSTOMER";
        this.meterNo = meterNo;
        this.employeeId = null;
    }

    public void loginEmployee(String employeeId) {
        this.role = "EMPLOYEE";
        this.employeeId = employeeId;
        this.meterNo = null;
    }

    public String getRole() {
        return role;
    }

    public String getMeterNo() {
        return meterNo;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setMeterNo(String meterNo) {
        this.meterNo = meterNo;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void requireCustomer() {

        if (!"CUSTOMER".equals(role)) {
            throw new ValidationException(
                    "Customer login required");
        }
    }
                                                        //method overloading
    public void requireCustomer(String meterNo) {

        requireCustomer();

        if (!meterNo.equals(this.meterNo)) {
            throw new ValidationException(
                    "You can only access your own account");
        }
    }

    public void requireEmployee() {

        if (!"EMPLOYEE".equals(role)) {
            throw new ValidationException(
                    "Employee login required");
        }
    }
}