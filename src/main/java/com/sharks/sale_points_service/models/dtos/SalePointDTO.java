package com.sharks.sale_points_service.models.dtos;

import com.sharks.sale_points_service.models.SalePoint;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SalePointDTO {

    private Long id;
    private String name;

    public SalePointDTO(SalePoint salePoint) {
        this.id = salePoint.getId();
        this.name = salePoint.getName();
    }
}
