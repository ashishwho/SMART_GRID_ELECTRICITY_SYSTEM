package com.smartgrid.smartgridelectricitysystem.repository;

import com.smartgrid.smartgridelectricitysystem.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByMeterNo(String meterNo);
}