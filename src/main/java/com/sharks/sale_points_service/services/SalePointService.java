package com.sharks.sale_points_service.services;

import java.util.List;

import com.sharks.sale_points_service.models.SalePoint;
import com.sharks.sale_points_service.models.dtos.NewSalePoint;
import com.sharks.sale_points_service.models.dtos.SalePointDTO;

public interface SalePointService {

    List<SalePointDTO> getAllSalePointDTOs();

    SalePoint getSalePointById(Long id);

    SalePointDTO getSalePointDTOById(Long id);

    SalePointDTO createSalePoint(NewSalePoint newSalePoint);

    SalePointDTO updateSalePoint(Long id, NewSalePoint newSalePoint);

    void deleteSalePoint(Long id);
}
