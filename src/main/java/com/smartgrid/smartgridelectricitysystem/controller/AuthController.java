package com.smartgrid.smartgridelectricitysystem.controller;

import com.smartgrid.smartgridelectricitysystem.model.Customer;
import com.smartgrid.smartgridelectricitysystem.model.Employee;
import com.smartgrid.smartgridelectricitysystem.service.AuthService;
import com.smartgrid.smartgridelectricitysystem.service.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    public ResponseEntity<Map<String,Object>> customerLogin(@RequestBody Map<String, String> body) {
        String meterNo = body.get("meterNo");
        String password = body.get("password");

        Customer customer = authService.customerLogin(meterNo, password);
        Map<String,Object> Response =new HashMap<String,Object>();

        Response.put("message","Customer login successful");
        Response.put("customer",customer);
        return ResponseEntity.ok(Response);
    }

    // POST /api/auth/employee/login
    @PostMapping("/employee/login")
    public ResponseEntity<Map<String,Object>> employeeLogin(@RequestBody Map<String, String> body) {
        String employeeId = body.get("employeeId");
        String password = body.get("password");

        Employee employee = authService.employeeLogin(employeeId, password);

        Map<String,Object> Response =new HashMap<String,Object>();
        Response.put("message","Employee login successful");
        Response.put("employee",employee);
        return ResponseEntity.ok(Response);
    }

    // POST /api/auth/logout
    @PostMapping("/logout")
    public ResponseEntity<Map<String,Object>> logout(){

        authService.logout();

        return ResponseEntity.ok(
                Map.of("message", "logged out successfully")
        );
    }

   // POST /api/auth/session
   @GetMapping("/session")
   public ResponseEntity<Map<String,Object>> currentSession() {

       Map<String,Object> response =
               new HashMap<String,Object>();

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