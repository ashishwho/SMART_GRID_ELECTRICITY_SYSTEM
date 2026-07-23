package com.smartgrid.smartgridelectricitysystem.controller;

import com.smartgrid.smartgridelectricitysystem.exception.ValidationException;
import com.smartgrid.smartgridelectricitysystem.model.*;
import com.smartgrid.smartgridelectricitysystem.service.BillService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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

    // POST /api/bills
    @PostMapping
    public ResponseEntity<Map<String,Object>> createBill(
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

        double units;
        try{
            units=Double.parseDouble(body.get("units"));
        }
        catch (NumberFormatException e){
            throw new ValidationException("units must be a valid number");
        }

        double rate;
        try{
            rate=Double.parseDouble(body.get("rate"));
        }
        catch (NumberFormatException e){
            throw new ValidationException("rate must be a valid number");
        }


        Bill bill =
                billService.createBill(
                        meterNo,
                        year,
                        month,
                        rate,
                        units
                );

        Map<String,Object> Response = new HashMap<String,Object>();
        Response.put("message", "Bill created successfully");
        Response.put("bill", bill);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Response);    }

    // POST /api/bills/{billId}/pay
    @PostMapping("/{billId}/pay")
    public ResponseEntity<Map<String, Object>> payBill(
            @RequestBody Map<String, String> body, @PathVariable Long billId) {

        String meterNo = body.get("meterNo");

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

        Bill bill =
                billService.payBill(
                        meterNo,
                        billId,
                        walletAmount,
                        bankPassword
                );
        Map<String,Object> response=new HashMap<String,Object>();
        response.put("message", "Bill paid successfully");
        response.put("bill", bill);
        return ResponseEntity.ok(response);
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


    // GET /api/bills?fromYear=2026&fromMonth=1&toYear=2026&toMonth=6&status=PENDING
    @GetMapping
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