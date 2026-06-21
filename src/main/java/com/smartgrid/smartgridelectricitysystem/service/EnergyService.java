package com.smartgrid.smartgridelectricitysystem.service;

import com.smartgrid.smartgridelectricitysystem.exception.DuplicateResourceException;
import com.smartgrid.smartgridelectricitysystem.exception.ResourceNotFoundException;
import com.smartgrid.smartgridelectricitysystem.exception.ValidationException;
import com.smartgrid.smartgridelectricitysystem.model.Customer;
import com.smartgrid.smartgridelectricitysystem.model.EnergyRecord;
import com.smartgrid.smartgridelectricitysystem.repository.CustomerRepository;
import com.smartgrid.smartgridelectricitysystem.repository.EnergyRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;

@Service
public class EnergyService {

    private final EnergyRecordRepository energyRecordRepository;
    private final CustomerRepository customerRepository;
    private final WalletService walletService;
    private final SessionService sessionService;

    public EnergyService(EnergyRecordRepository energyRecordRepository,
                         CustomerRepository customerRepository,
                         WalletService walletService,
                         SessionService sessionService) {
        this.energyRecordRepository = energyRecordRepository;
        this.customerRepository = customerRepository;
        this.walletService = walletService;
        this.sessionService = sessionService;
    }

    @Transactional
    public EnergyRecord addEnergyRecord(String meterNo, int year, int month, double unitsProduced, double ratePerUnit) {

        sessionService.requireEmployee();

        Customer customer = customerRepository.findById(meterNo)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found: " + meterNo));

        if (!customer.isHasSolarPanel()) {
            throw new ValidationException(
                    "Customer does not have a energy production source");
        }
        if(unitsProduced <= 0 || ratePerUnit <= 0) {
            throw new ValidationException("unitsProduced and ratePerUnit must be greater than 0");
        }


        if(month<1||month>12){
            throw new ValidationException("month must be between 1 and 12");
        }

        YearMonth requested= YearMonth.of(year,month);
        if(!requested.isBefore(YearMonth.now())){
            throw new ValidationException("Energy record can be created for past months only");
        }



        boolean recordExists =
                energyRecordRepository.findByCustomerMeterNo(meterNo).stream().anyMatch(
                        r->YearMonth.from(
                                        r.getRecordDate())
                                .equals(requested));

        if(recordExists){
            throw new DuplicateResourceException("Energy record already exits for customer "+meterNo + " for month "+ month+ " year "+ year);
        }

        EnergyRecord record = new EnergyRecord(
                unitsProduced,
                ratePerUnit,
                requested.atEndOfMonth(),
                customer
        );

        EnergyRecord savedRecord =
                energyRecordRepository.save(record);

        walletService.creditWallet(meterNo, savedRecord.getVirtualMoney());

        return savedRecord;
    }

    public List<EnergyRecord> getAllEnergyRecords() {
        sessionService.requireEmployee();
        return energyRecordRepository.findAll();
    }

    public List<EnergyRecord> getEnergyByCustomer(String meterNo) {

        if("EMPLOYEE".equals(sessionService.getRole())){
             //emp can access any customer's energy_record data
        }
        else{
            sessionService.requireCustomer(meterNo);
        }

        if(!customerRepository.findById(meterNo).isPresent()) {
            throw new ResourceNotFoundException("Customer not found");
        }
        return energyRecordRepository.findByCustomerMeterNo(meterNo);
    }
}