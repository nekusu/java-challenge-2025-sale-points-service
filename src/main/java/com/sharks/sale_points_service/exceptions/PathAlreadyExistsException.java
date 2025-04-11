package com.sharks.sale_points_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PathAlreadyExistsException extends RuntimeException {

    public PathAlreadyExistsException(Long idA, Long idB) {
        super("Path already exists for sale points with ids " + idA + " and " + idB);
    }

    public PathAlreadyExistsException(String message) {
        super(message);
    }
}
