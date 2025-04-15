package com.sharks.sale_points_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SalePointsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalePointsServiceApplication.class, args);
	}

}
