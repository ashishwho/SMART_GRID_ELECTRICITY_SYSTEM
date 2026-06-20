package com.smartgrid.smartgridelectricitysystem.controller;

import com.smartgrid.smartgridelectricitysystem.exception.ValidationException;
import com.smartgrid.smartgridelectricitysystem.model.*;
import com.smartgrid.smartgridelectricitysystem.service.BillService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "*")
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    // POST /api/bills/create
    @PostMapping("/create")
    public ResponseEntity<Bill> createBill(
            @RequestBody Map<String, String> body) {

        String meterNo =
                body.get("meterNo");

        int year;

        try {
            year = Integer.parseInt(
                    body.get("year"));
        } catch (NumberFormatException e) {
            throw new ValidationException(
                    "Year must be a valid number");
        }

        int month;

        try {
            month = Integer.parseInt(
                    body.get("month"));
        } catch (NumberFormatException e) {
            throw new ValidationException(
                    "Month must be a valid number");
        }

        double totalAmount;

        try {
            totalAmount = Double.parseDouble(
                    body.get("totalAmount"));
        } catch (NumberFormatException e) {
            throw new ValidationException(
                    "Total amount must be a valid number");
        }

        Bill bill =
                billService.createBill(
                        meterNo,
                        year,
                        month,
                        totalAmount
                );

        return ResponseEntity.ok(bill);
    }

    // POST /api/bills/pay
    @PostMapping("/pay")
    public ResponseEntity<Map<String, Object>> payBill(
            @RequestBody Map<String, String> body) {

        String meterNo = body.get("meterNo");

        Long billId;

        try {
            billId = Long.parseLong(
                    body.get("billId"));
        } catch (NumberFormatException e) {
            throw new ValidationException(
                    "Invalid bill id");
        }

        double walletAmount;

        try {
            walletAmount = Double.parseDouble(
                    body.get("walletAmount"));
        } catch (NumberFormatException e) {
            throw new ValidationException(
                    "Wallet amount must be a valid number");
        }

        String bankPassword =
                body.get("bankPassword");

        boolean result =
                billService.payBill(
                        meterNo,
                        billId,
                        walletAmount,
                        bankPassword
                );

        return ResponseEntity.ok(
                Map.of(
                        "success", result,
                        "message",
                        "Bill paid successfully"
                )
        );
    }

    // GET /api/bills/pending/{meterNo}
    @GetMapping("/pending/{meterNo}")
    public ResponseEntity<List<Bill>> getPendingBills(
            @PathVariable String meterNo) {

        return ResponseEntity.ok(
                billService.getPendingBills(meterNo));
    }

    // GET /api/bills/paid/{meterNo}
    @GetMapping("/paid/{meterNo}")
    public ResponseEntity<List<Bill>> getPaidBills(@PathVariable String meterNo) {
        return ResponseEntity.ok(billService.getPaidBills(meterNo));
    }


    // GET /api/bills/search?fromYear=2026&fromMonth=1&toYear=2026&toMonth=6&status=PENDING
    @GetMapping("/search")
    public ResponseEntity<List<Bill>> searchBills(
            @RequestParam int fromYear,
            @RequestParam int fromMonth,
            @RequestParam int toYear,
            @RequestParam int toMonth,
            @RequestParam String status) {

        BillStatus billStatus;

        try {
            billStatus =
                    BillStatus.valueOf(
                            status.toUpperCase());
        } catch (Exception e) {
            throw new ValidationException(
                    "Invalid bill status");
        }

        return ResponseEntity.ok(
                billService.searchBills(
                        fromYear,
                        fromMonth,
                        toYear,
                        toMonth,
                        billStatus
                )
        );
    }

}