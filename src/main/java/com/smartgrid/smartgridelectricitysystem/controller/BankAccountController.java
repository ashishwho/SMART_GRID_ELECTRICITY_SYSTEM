package com.smartgrid.smartgridelectricitysystem.controller;

import com.smartgrid.smartgridelectricitysystem.service.BankAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/bank-accounts")
@CrossOrigin(origins = "*")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(
            BankAccountService bankAccountService) {

        this.bankAccountService =
                bankAccountService;
    }

    // POST /api/bank-accounts/utility/add
    @PostMapping("/utility/add")
    public ResponseEntity<?> addUtilityAccount(
            @RequestBody Map<String, String> body) {

        String bankAccNo =
                body.get("bankAccNo");

        String utilityName =
                body.get("utilityName");

        return ResponseEntity.ok(
                bankAccountService
                        .addUtilityAccount(
                                bankAccNo,
                                utilityName
                        )
        );
    }
}