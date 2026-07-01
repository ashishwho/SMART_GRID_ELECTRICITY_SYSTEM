package com.smartgrid.smartgridelectricitysystem.controller;

import com.smartgrid.smartgridelectricitysystem.exception.ValidationException;
import com.smartgrid.smartgridelectricitysystem.model.Customer;
import com.smartgrid.smartgridelectricitysystem.model.CustomerType;
import com.smartgrid.smartgridelectricitysystem.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {

        this.customerService = customerService;
    }

    // GET /api/customers
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    // GET /api/customers/{meterNo}
    @GetMapping("/{meterNo}")
    public ResponseEntity<Customer> getCustomer(@PathVariable String meterNo) {
        return ResponseEntity.ok(customerService.getCustomerByMeterNo(meterNo));
    }

    // POST /api/customers/add
    @PostMapping("/add")
    public ResponseEntity<Customer> addCustomer(
            @RequestBody Map<String, Object> body) {

        String meterNo = (String) body.get("meterNo");
        String name = (String) body.get("name");
        String password = (String) body.get("password");
        String bankAccNo = (String) body.get("bankAccNo");

        CustomerType type;

        try {
            type = CustomerType.valueOf(
                    ((String) body.get("type")).toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    "Invalid customer type");
        }

        boolean hasSolarPanel =
                (Boolean) body.getOrDefault(
                        "hasSolarPanel",
                        false);

        Customer saved =
                customerService.addCustomer(
                        meterNo,
                        name,
                        password,
                        type,
                        hasSolarPanel,
                        bankAccNo
                );

        return ResponseEntity.ok(saved);
    }

    // PUT /api/customers/{meterNo}/toggle-connection
    @PutMapping("/{meterNo}/toggle-connection")
    public ResponseEntity<Map<String,Object>> toggleConnection(@PathVariable String meterNo) {
        boolean newStatus = customerService.toggleConnection(meterNo);
        return ResponseEntity.ok(Map.of(
                "meterNo", meterNo,
                "connectionStatus", newStatus
        ));
    }

    // PUT /api/customers/cut-connections/{month}
    @PutMapping("/cut-overdue-connections")
    public ResponseEntity<Map<String, Object>>
    cutOverdueConnections() {

        int count =
                customerService.cutOverdueConnections();

        return ResponseEntity.ok(
                Map.of(
                        "connectionsCut", count,
                        "rule",
                        "Pending bill older than 3 months"
                )
        );
    }

    @PutMapping("/{meterNo}/toggle-hasSolarPanel")
    public ResponseEntity<Map<String,Object>>
    toggleHasSolarPanel(@PathVariable String meterNo) {
        boolean newStatus=customerService.toggleHasSolarPanel(meterNo);
        return ResponseEntity.ok(Map.of(
                "meterNo", meterNo,
               "hasSolarPanel", newStatus
        ));
    }


}