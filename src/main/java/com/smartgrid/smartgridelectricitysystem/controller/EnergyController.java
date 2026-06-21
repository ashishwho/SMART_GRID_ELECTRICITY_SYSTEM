package com.smartgrid.smartgridelectricitysystem.controller;

import com.smartgrid.smartgridelectricitysystem.exception.ValidationException;
import com.smartgrid.smartgridelectricitysystem.model.*;
import com.smartgrid.smartgridelectricitysystem.repository.CustomerRepository;
import com.smartgrid.smartgridelectricitysystem.service.EnergyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/energy")
@CrossOrigin(origins = "*")
public class EnergyController {

    private final EnergyService energyService;
    private final CustomerRepository customerRepository;

    public EnergyController(EnergyService energyService,
                            CustomerRepository customerRepository) {
        this.energyService = energyService;
        this.customerRepository = customerRepository;
    }

    // POST /api/energy/add
    @PostMapping("/add")
    public ResponseEntity<EnergyRecord> addEnergyRecord(
            @RequestBody Map<String, String> body) {

        String meterNo = body.get("meterNo");

        String source = body.get("sourceType");

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

        return ResponseEntity.ok(saved);
    }

    // GET /api/energy
    @GetMapping
    public ResponseEntity<List<EnergyRecord>> getAllEnergyRecords() {
        return ResponseEntity.ok(energyService.getAllEnergyRecords());
    }

    // GET /api/energy/{meterNo}
    @GetMapping("/{meterNo}")
    public ResponseEntity<List<EnergyRecord>> getEnergyByCustomer(@PathVariable String meterNo) {
        return ResponseEntity.ok(energyService.getEnergyByCustomer(meterNo));
    }
}