package com.smartgrid.smartgridelectricitysystem;

import com.smartgrid.smartgridelectricitysystem.service.BankAccountService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class SmartGridElectricitySystemApplication implements CommandLineRunner {

	private final BankAccountService bankAccountService;

	public SmartGridElectricitySystemApplication(BankAccountService bankAccountService) {
		this.bankAccountService = bankAccountService;
	}

	public static void main(String[] args) {
		SpringApplication.run(SmartGridElectricitySystemApplication.class, args);
	}

	@Override
	public void run(String... args) {
		// Only runs utility account creation on startup
		//bankAccountService.createUtilityAccountIfNotExists();
	}
}