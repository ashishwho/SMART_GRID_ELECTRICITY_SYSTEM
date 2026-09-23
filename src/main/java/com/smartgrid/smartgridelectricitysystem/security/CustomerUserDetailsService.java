package com.smartgrid.smartgridelectricitysystem.security;

import com.smartgrid.smartgridelectricitysystem.model.Customer;
import com.smartgrid.smartgridelectricitysystem.repository.CustomerRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    public CustomerUserDetailsService(
            CustomerRepository customerRepository) {

        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String meterNo)
            throws UsernameNotFoundException {

        Customer customer =
                customerRepository.findById(meterNo)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "Customer not found: " + meterNo));

        return new CustomerUserDetails(customer);
    }
}