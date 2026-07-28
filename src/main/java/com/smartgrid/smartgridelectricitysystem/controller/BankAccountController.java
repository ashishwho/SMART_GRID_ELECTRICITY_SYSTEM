package com.smartgrid.smartgridelectricitysystem.controller;

import com.smartgrid.smartgridelectricitysystem.model.UtilityAccount;
import com.smartgrid.smartgridelectricitysystem.service.BankAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/bankaccounts")
@CrossOrigin(origins = "*")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(
            BankAccountService bankAccountService) {

        this.bankAccountService =
                bankAccountService;
    }

    // POST /api/bankAccounts/utility
    @PostMapping("/utility")
    public ResponseEntity<Map<String,Object>> addUtilityAccount(
            @RequestBody Map<String, String> body) {

        String bankAccNo =
                body.get("bankAccNo");

        String utilityName =
                body.get("utilityName");

        UtilityAccount utilityAccount =  bankAccountService.addUtilityAccount(bankAccNo, utilityName);

        Map<String,Object> Response = new HashMap<String,Object>();
        Response.put("message", "utility account added successfully");
        Response.put("utilityAccount", utilityAccount);

        return ResponseEntity.ok(Response);
    }
}