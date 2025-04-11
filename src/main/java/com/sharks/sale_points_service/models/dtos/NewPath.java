package com.sharks.sale_points_service.models.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record NewPath(@NotNull Long idA, @NotNull Long idB, @NotNull @Min(0) Double cost) {
}
