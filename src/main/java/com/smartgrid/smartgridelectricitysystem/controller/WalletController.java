package com.smartgrid.smartgridelectricitysystem.controller;

import com.smartgrid.smartgridelectricitysystem.exception.ValidationException;
import com.smartgrid.smartgridelectricitysystem.model.Wallet;
import com.smartgrid.smartgridelectricitysystem.service.BankAccountService;
import com.smartgrid.smartgridelectricitysystem.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@CrossOrigin(origins = "*")
public class WalletController {

    private final WalletService walletService;
    private final BankAccountService bankAccountService;

    public WalletController(WalletService walletService,
                            BankAccountService bankAccountService) {
        this.walletService = walletService;
        this.bankAccountService = bankAccountService;
    }

    // GET /api/wallet/{meterNo}
    @GetMapping("/{meterNo}")
    public ResponseEntity<Wallet> getWallet(@PathVariable String meterNo) {
        return ResponseEntity.ok(walletService.getWalletByMeterNo(meterNo));
    }

    // POST /api/wallet/convert
    @PostMapping("/convert")
    public ResponseEntity<?> convertToBank(@RequestBody Map<String, String> body) {
        String meterNo = body.get("meterNo");
        double amount;

        try {
            amount = Double.parseDouble(
                    body.get("amount"));
        } catch (NumberFormatException e) {
            throw new ValidationException(
                    "Amount must be a valid number");
        }

        String bankPassword = body.get("bankPassword");
        boolean result = bankAccountService.convertVirtualToBank(
                meterNo, amount, bankPassword);
        return ResponseEntity.ok(Map.of(
                "success", result,
                "message", "Virtual money converted to bank successfully"
        ));
    }
}
