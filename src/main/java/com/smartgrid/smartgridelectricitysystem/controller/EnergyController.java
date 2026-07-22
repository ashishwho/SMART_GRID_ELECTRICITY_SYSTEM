package com.smartgrid.smartgridelectricitysystem.controller;

import com.smartgrid.smartgridelectricitysystem.exception.ValidationException;
import com.smartgrid.smartgridelectricitysystem.model.*;
import com.smartgrid.smartgridelectricitysystem.repository.CustomerRepository;
import com.smartgrid.smartgridelectricitysystem.service.EnergyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/energy")
@CrossOrigin(origins = "*")
public class EnergyController {

    private final EnergyService energyService;

    public EnergyController(EnergyService energyService,
                            CustomerRepository customerRepository) {
        this.energyService = energyService;
    }

    // POST /api/energy/add
    @PostMapping("/add")
    public ResponseEntity<Map<String,Object>> addEnergyRecord(
            @RequestBody Map<String, String> body) {

        String meterNo = body.get("meterNo");


        double units;
        try {
            units = Double.parseDouble(
                    body.get("unitsProduced"));
        } catch (NumberFormatException e) {
            throw new ValidationException(
                    "Units produced must be a valid number");
        }

        double rate;
        try {
            rate = Double.parseDouble(
                    body.get("ratePerUnit"));
        } catch (NumberFormatException e) {
            throw new ValidationException(
                    "Rate per unit must be a valid number");
        }

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

        EnergyRecord saved =
                energyService.addEnergyRecord(
                        meterNo,
                        year,
                        month,
                        units,
                        rate);
        Map<String,Object> Response = new HashMap<String,Object>();
        Response.put("message", "Energy record added successfully");
        Response.put("customer", saved);

        return ResponseEntity.status(HttpStatus.CREATED).body(Response);
    }

    // GET /api/energy/search?fromYear=20XX&fromMonth=XX&toYear=20XX&toMonth=XX
    @GetMapping("/search")
    public ResponseEntity<List<EnergyRecord>> searchBills(
            @RequestParam int fromYear,
            @RequestParam int fromMonth,
            @RequestParam int toYear,
            @RequestParam int toMonth) {


        return ResponseEntity.ok(
                energyService.searchEnergyRecords(
                        fromMonth,
                        fromYear,
                        toMonth,
                        toYear
                )
        );
    }

    // GET /api/energy/{meterNo}
    @GetMapping("/{meterNo}")
    public ResponseEntity<List<EnergyRecord>> getEnergyByCustomer(@PathVariable String meterNo) {
        return ResponseEntity.ok(energyService.getEnergyByCustomer(meterNo));
    }
}