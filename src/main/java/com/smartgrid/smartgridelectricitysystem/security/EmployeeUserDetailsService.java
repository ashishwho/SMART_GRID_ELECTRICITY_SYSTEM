package com.smartgrid.smartgridelectricitysystem.security;

import com.smartgrid.smartgridelectricitysystem.model.Employee;
import com.smartgrid.smartgridelectricitysystem.repository.EmployeeRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class EmployeeUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    public EmployeeUserDetailsService(
            EmployeeRepository employeeRepository) {

        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String employeeId)
            throws UsernameNotFoundException {

        Employee employee =
                employeeRepository.findByEmployeeId(employeeId)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "Employee not found: " + employeeId));

        return new EmployeeUserDetails(employee);
    }
}