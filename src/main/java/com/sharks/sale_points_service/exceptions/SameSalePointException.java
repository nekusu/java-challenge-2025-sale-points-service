package com.sharks.sale_points_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SameSalePointException extends RuntimeException {

    public SameSalePointException(Long id) {
        super("Sale point with id " + id + " cannot be the same as the other sale point");
    }

    public SameSalePointException(String message) {
        super(message);
    }
}
