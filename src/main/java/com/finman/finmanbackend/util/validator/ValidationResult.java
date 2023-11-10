package com.finman.finmanbackend.util.validator;

public interface ValidationResult {
    boolean isSuccess();
    default boolean isFailure() {
        return !this.isSuccess();
    }
}
