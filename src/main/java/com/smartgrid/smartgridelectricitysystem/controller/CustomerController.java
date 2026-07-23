package com.smartgrid.smartgridelectricitysystem.controller;

import com.smartgrid.smartgridelectricitysystem.exception.ValidationException;
import com.smartgrid.smartgridelectricitysystem.model.Customer;
import com.smartgrid.smartgridelectricitysystem.model.CustomerType;
import com.smartgrid.smartgridelectricitysystem.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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

    // POST /api/customers
    @PostMapping
    public ResponseEntity<Map<String,Object>> addCustomer(
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
        Map<String,Object> Response = new HashMap<String,Object>();
        Response.put("message", "Customer added successfully");
        Response.put("Customer", saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(Response);
    }

    // PATCH /api/customers/{meterNo}/connection
    @PatchMapping("/{meterNo}/connection")
    public ResponseEntity<Map<String,Object>> updateConnection(@RequestBody Map<String,Object> body, @PathVariable String meterNo) {
        boolean newStatus = (boolean)body.get("connectionStatus");
        customerService.updateConnection(meterNo,newStatus);
        return ResponseEntity.ok(Map.of(
                "meterNo", meterNo,
                "connectionStatus", newStatus
        ));
    }

    // POST /api/customers/cut-overdue-connections
    @PostMapping("/cut-overdue-connections")
    public ResponseEntity<Map<String, Object>>
    cutOverdueConnections() {

        int count =
                customerService.cutOverdueConnections();

        return ResponseEntity.ok(
                Map.of(
                        "connectionsCut", count,
                        "rule", "Pending bill older than 3 months"
                )
        );
    }

    //PATCH /api/customers/{}/solar-panel
    @PatchMapping("/{meterNo}/solar-panel")
    public ResponseEntity<Map<String,Object>>
    updateHasSolarPanel(@RequestBody Map<String, Object> body, @PathVariable String meterNo) {
        boolean newStatus=(boolean)body.get("hasSolarPanel");
        customerService.updateHasSolarPanel(meterNo,newStatus);
        return ResponseEntity.ok(Map.of(
                "meterNo", meterNo,
               "hasSolarPanel", newStatus
        ));
    }

}