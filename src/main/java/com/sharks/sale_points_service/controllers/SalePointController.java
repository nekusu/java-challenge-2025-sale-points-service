package com.sharks.sale_points_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sharks.sale_points_service.models.dtos.NewSalePoint;
import com.sharks.sale_points_service.models.dtos.SalePointDTO;
import com.sharks.sale_points_service.services.SalePointService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/sale-points")
public class SalePointController {

    private final SalePointService salePointService;

    public SalePointController(SalePointService salePointService) {
        this.salePointService = salePointService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SalePointDTO> getAllSalePoints() {
        return salePointService.getAllSalePointDTOs();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SalePointDTO getSalePoint(@PathVariable Long id) {
        return salePointService.getSalePointDTOById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SalePointDTO createSalePoint(@RequestBody @Valid NewSalePoint newSalePoint) {
        return salePointService.createSalePoint(newSalePoint);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SalePointDTO updateSalePoint(@PathVariable Long id, @RequestBody @Valid NewSalePoint newSalePoint) {
        return salePointService.updateSalePoint(id, newSalePoint);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSalePoint(@PathVariable Long id) {
        salePointService.deleteSalePoint(id);
    }
}
