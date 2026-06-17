package com.smartgrid.smartgridelectricitysystem.controller;

import com.smartgrid.smartgridelectricitysystem.model.Customer;
import com.smartgrid.smartgridelectricitysystem.model.Employee;
import com.smartgrid.smartgridelectricitysystem.service.AuthService;
import com.smartgrid.smartgridelectricitysystem.service.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final SessionService sessionService;

    public AuthController(AuthService authService,
                          SessionService sessionService) {

        this.authService = authService;
        this.sessionService = sessionService;
    }

    @RequestMapping("/")
    public String greet(){
        return "Welcome to Gujarat state Electricity board. You can login as Customer or Employee";
    }

    // POST /api/auth/customer/login
    @PostMapping("/customer/login")
    public ResponseEntity<Customer> customerLogin(@RequestBody Map<String, String> body) {
        String meterNo = body.get("meterNo");
        String password = body.get("password");
        Customer customer = authService.customerLogin(meterNo, password);
        return ResponseEntity.ok(customer);
    }

    // POST /api/auth/employee/login
    @PostMapping("/employee/login")
    public ResponseEntity<Employee> employeeLogin(@RequestBody Map<String, String> body) {
        String employeeId = body.get("employeeId");
        String password = body.get("password");
        Employee employee = authService.employeeLogin(employeeId, password);
        return ResponseEntity.ok(employee);
    }

   // POST /api/auth/session
   @GetMapping("/session")
   public ResponseEntity<?> currentSession() {

       Map<String,Object> response =
               new java.util.HashMap<>();

       response.put(
               "role",
               sessionService.getRole());

       response.put(
               "meterNo",
               sessionService.getMeterNo());

       response.put(
               "employeeId",
               sessionService.getEmployeeId());

       return ResponseEntity.ok(response);
   }
}