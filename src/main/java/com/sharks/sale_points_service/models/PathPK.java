package com.sharks.sale_points_service.models;

import java.io.Serializable;
import java.util.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PathPK implements Serializable {

    private Long salePointA;
    private Long salePointB;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof PathPK))
            return false;
        PathPK pathPK = (PathPK) o;
        return salePointA.equals(pathPK.salePointA) && salePointB.equals(pathPK.salePointB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(salePointA, salePointB);
    }
}
