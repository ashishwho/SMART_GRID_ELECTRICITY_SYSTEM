package com.smartgrid.smartgridelectricitysystem.repository;

import com.smartgrid.smartgridelectricitysystem.model.Bill;
import com.smartgrid.smartgridelectricitysystem.model.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {

    // Get all bills for one customer
    List<Bill> findByCustomerMeterNo(String meterNo);

    // Get pending or paid bills for one customer
    List<Bill> findByCustomerMeterNoAndStatus(
            String meterNo,
            BillStatus status);

    // Get all bills with a given status
    List<Bill> findByStatus(BillStatus status);

    // Get all bills before a given date with a given status
    List<Bill> findByStatusAndBillDateBefore(
            BillStatus status,
            LocalDate billDate);

    // Count pending/paid bills of a customer
    int countByCustomerMeterNoAndStatus(
            String meterNo,
            BillStatus status);

    Optional<Bill> findByBillIdAndStatus(Long billId, BillStatus status);

    List<Bill> findByStatusAndBillDateBetween(
            BillStatus status,
            LocalDate startDate,
            LocalDate endDate);
}