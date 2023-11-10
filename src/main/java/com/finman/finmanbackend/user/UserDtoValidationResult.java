package com.finman.finmanbackend.user;

import com.finman.finmanbackend.util.validator.ValidationResult;

public enum UserDtoValidationResult implements ValidationResult {
    VALID,
    PASSWORD_TOO_SHORT,
    PASSWORD_INVALID_CHARACTERS,
    PASSWORD_NULL,
    EMAIL_NOT_EMAIL,
    EMAIL_TOO_LONG,
    EMAIL_NULL;


    @Override
    public boolean isSuccess() {
        return this == VALID;
    }
}
