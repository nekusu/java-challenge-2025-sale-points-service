package com.sharks.sale_points_service.models;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@IdClass(PathPK.class)
@Table(name = "paths")
public class Path {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_a", referencedColumnName = "id", nullable = false)
    private SalePoint salePointA;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_b", referencedColumnName = "id", nullable = false)
    private SalePoint salePointB;

    @Setter
    @NonNull
    @Column(nullable = false)
    private Double cost;
}
