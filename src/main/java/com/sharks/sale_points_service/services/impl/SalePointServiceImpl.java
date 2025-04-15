package com.sharks.sale_points_service.services.impl;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.sharks.sale_points_service.exceptions.NameAlreadyExistsException;
import com.sharks.sale_points_service.exceptions.SalePointNotFoundException;
import com.sharks.sale_points_service.models.SalePoint;
import com.sharks.sale_points_service.models.dtos.NewSalePoint;
import com.sharks.sale_points_service.models.dtos.SalePointDTO;
import com.sharks.sale_points_service.repositories.SalePointRepository;
import com.sharks.sale_points_service.services.SalePointService;

@Service
public class SalePointServiceImpl implements SalePointService {

    private final SalePointRepository salePointRepository;

    public SalePointServiceImpl(SalePointRepository salePointRepository) {
        this.salePointRepository = salePointRepository;
    }

    @Override
    public List<SalePointDTO> getAllSalePointDTOs() {
        return salePointRepository.findAll().stream().map(SalePointDTO::new).toList();
    }

    @Override
    public SalePoint getSalePointById(Long id) {
        return salePointRepository.findById(id).orElseThrow(() -> new SalePointNotFoundException(id));
    }

    @Override
    @Cacheable(value = "salePoints", key = "#id")
    public SalePointDTO getSalePointDTOById(Long id) {
        return new SalePointDTO(getSalePointById(id));
    }

    @Override
    @CachePut(value = "salePoints", key = "#result.id")
    public SalePointDTO createSalePoint(NewSalePoint newSalePoint) {
        validateSalePoint(newSalePoint);
        SalePoint salePoint = new SalePoint(newSalePoint.name());
        SalePoint savedSalePoint = salePointRepository.save(salePoint);
        return new SalePointDTO(savedSalePoint);
    }

    @Override
    @CachePut(value = "salePoints", key = "#id")
    public SalePointDTO updateSalePoint(Long id, NewSalePoint newSalePoint) {
        SalePoint existingSalePoint = getSalePointById(id);
        existingSalePoint.setName(newSalePoint.name());
        SalePoint updatedSalePoint = salePointRepository.save(existingSalePoint);
        return new SalePointDTO(updatedSalePoint);
    }

    @Override
    @CacheEvict(value = "salePoints", key = "#id")
    public void deleteSalePoint(Long id) {
        SalePoint existingSalePoint = getSalePointById(id);
        salePointRepository.delete(existingSalePoint);
    }

    private void validateSalePoint(NewSalePoint newSalePoint) {
        if (salePointRepository.existsByName(newSalePoint.name()))
            throw new NameAlreadyExistsException();
    }
}
