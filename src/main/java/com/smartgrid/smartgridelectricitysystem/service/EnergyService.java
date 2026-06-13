package com.smartgrid.smartgridelectricitysystem.service;

import com.smartgrid.smartgridelectricitysystem.exception.ResourceNotFoundException;
import com.smartgrid.smartgridelectricitysystem.exception.ValidationException;
import com.smartgrid.smartgridelectricitysystem.model.Customer;
import com.smartgrid.smartgridelectricitysystem.model.EnergyRecord;
import com.smartgrid.smartgridelectricitysystem.model.EnergySource;
import com.smartgrid.smartgridelectricitysystem.repository.CustomerRepository;
import com.smartgrid.smartgridelectricitysystem.repository.EnergyRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
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
    public EnergyRecord addEnergyRecord(String meterNo, EnergySource source, double unitsProduced, double ratePerUnit) {

        sessionService.requireEmployee();
        Customer customer = customerRepository.findById(meterNo)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found: " + meterNo));

        if (!customer.isHasSolarPanel()) {
            throw new ValidationException(
                    "Customer does not have a solar panel");
        }
        if(unitsProduced <= 0 || ratePerUnit <= 0) {
            throw new ValidationException("unitsProduced and ratePerUnit must be greater than 0");
        }


        EnergyRecord record = new EnergyRecord(
                source,
                unitsProduced,
                ratePerUnit,
                LocalDate.now(),
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