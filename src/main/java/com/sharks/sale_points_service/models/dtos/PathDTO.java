package com.sharks.sale_points_service.models.dtos;

import com.sharks.sale_points_service.models.Path;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PathDTO {

    private SalePointDTO salePointA;
    private SalePointDTO salePointB;
    private Double cost;

    public PathDTO(Path path) {
        this.salePointA = new SalePointDTO(path.getSalePointA());
        this.salePointB = new SalePointDTO(path.getSalePointB());
        this.cost = path.getCost();
    }
}
