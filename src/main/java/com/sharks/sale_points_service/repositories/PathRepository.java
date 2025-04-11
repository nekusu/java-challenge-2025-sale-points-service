package com.sharks.sale_points_service.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sharks.sale_points_service.models.Path;
import com.sharks.sale_points_service.models.PathPK;

public interface PathRepository extends JpaRepository<Path, PathPK> {

    List<Path> findBySalePointA_Id(Long idA);

    List<Path> findBySalePointB_Id(Long idB);

    Optional<Path> findBySalePointA_IdAndSalePointB_Id(Long idA, Long idB);

    Boolean existsBySalePointA_IdAndSalePointB_Id(Long idA, Long idB);
}
