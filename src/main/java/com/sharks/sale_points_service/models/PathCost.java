package com.sharks.sale_points_service.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PathCost {

    private final List<SalePointCost> path;
    private final Double totalCost;
}
