package com.sharks.sale_points_service.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class SalePointCost implements Comparable<SalePointCost> {

    @NonNull

    private Long id;

    private String name;

    @NonNull
    private Double cost;

    @Override
    public int compareTo(SalePointCost other) {
        return this.cost.compareTo(other.cost);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        SalePointCost that = (SalePointCost) obj;
        return this.cost.equals(that.cost);
    }

    @Override
    public int hashCode() {
        return cost.hashCode();
    }
}
