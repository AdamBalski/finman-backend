package com.finman.finmanbackend.util.validator;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class HttpValidationException extends ResponseStatusException {
    public <T> HttpValidationException(ValidationResult validationResult, Class<T> clazz) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, clazz.getName() + "_INVALID_" + validationResult.toString());
    }

    public <T> HttpValidationException(Class<T> clazz) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, clazz.getName() + "_INVALID");
    }
}
