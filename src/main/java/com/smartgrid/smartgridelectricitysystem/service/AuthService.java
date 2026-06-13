package com.smartgrid.smartgridelectricitysystem.service;

import com.smartgrid.smartgridelectricitysystem.exception.InvalidCredentialsException;
import com.smartgrid.smartgridelectricitysystem.model.Customer;
import com.smartgrid.smartgridelectricitysystem.model.Employee;
import com.smartgrid.smartgridelectricitysystem.repository.CustomerRepository;
import com.smartgrid.smartgridelectricitysystem.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final SessionService sessionService;

    public AuthService(CustomerRepository customerRepository,
                       EmployeeRepository employeeRepository,
                       SessionService sessionService) {

        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
        this.sessionService = sessionService;
    }

    public Customer customerLogin(
            String meterNo,
            String password) {

        Customer customer =
                customerRepository.findById(meterNo)
                        .filter(c ->
                                c.getPassword()
                                        .equals(password))
                        .orElseThrow(() ->
                                new InvalidCredentialsException(
                                        "Invalid meter number or password"));

        sessionService.loginCustomer(
                customer.getMeterNo());

        return customer;
    }

    public Employee employeeLogin(
            String employeeId,
            String password) {

        Employee employee =
                employeeRepository
                        .findByEmployeeIdAndPassword(
                                employeeId,
                                password)
                        .orElseThrow(() ->
                                new InvalidCredentialsException(
                                        "Invalid employee ID or password"));

        sessionService.loginEmployee(
                employee.getEmployeeId());

        return employee;
    }
}