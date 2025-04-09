package com.sharks.sale_points_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sharks.sale_points_service.models.SalePoint;

public interface SalePointRepository extends JpaRepository<SalePoint, Long> {

    boolean existsByName(String name);
}
