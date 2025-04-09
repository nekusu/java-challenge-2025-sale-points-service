package com.sharks.sale_points_service.models.dtos;

import jakarta.validation.constraints.NotBlank;

public record NewSalePoint(@NotBlank(message = "Name is required") String name) {
}
