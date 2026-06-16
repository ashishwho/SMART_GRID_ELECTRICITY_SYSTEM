package com.smartgrid.smartgridelectricitysystem.repository;

import com.smartgrid.smartgridelectricitysystem.model.UtilityAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilityAccountRepository
        extends JpaRepository<UtilityAccount,String> {
}
