package com.sharks.sale_points_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PathNotFoundException extends RuntimeException {

    public PathNotFoundException(Long idA, Long idB) {
        super("Path not found for sale points with ids " + idA + " and " + idB);
    }

    public PathNotFoundException(String message) {
        super(message);
    }
}
