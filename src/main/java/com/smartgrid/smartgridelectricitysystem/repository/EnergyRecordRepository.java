package com.smartgrid.smartgridelectricitysystem.repository;

import com.smartgrid.smartgridelectricitysystem.model.EnergyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnergyRecordRepository extends JpaRepository<EnergyRecord, Long> {
    List<EnergyRecord> findByCustomerMeterNo(String meterNo);

}