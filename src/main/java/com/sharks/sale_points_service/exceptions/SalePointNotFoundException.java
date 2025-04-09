package com.sharks.sale_points_service.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SalePointNotFoundException extends RuntimeException {

    public SalePointNotFoundException(Long id) {
        super("Sale point not found with id: " + id);
    }

    public SalePointNotFoundException(String message) {
        super(message);
    }
}
