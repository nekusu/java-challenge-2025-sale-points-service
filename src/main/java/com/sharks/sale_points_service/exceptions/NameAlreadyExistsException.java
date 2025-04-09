package com.sharks.sale_points_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class NameAlreadyExistsException extends RuntimeException {

    public NameAlreadyExistsException() {
        super("Name is already in use");
    }

    public NameAlreadyExistsException(String message) {
        super(message);
    }
}
