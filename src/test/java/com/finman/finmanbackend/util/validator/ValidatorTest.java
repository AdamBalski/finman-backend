package com.finman.finmanbackend.util.validator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidatorTest {
    Validator<Integer, ValidationResultEnum> failure1Validator = element -> ValidationResultEnum.FAILURE1;
    Validator<Integer, ValidationResultEnum> failure2Validator = element -> ValidationResultEnum.FAILURE2;
    Validator<Integer, ValidationResultEnum> successValidator = element -> ValidationResultEnum.SUCCESS;

    @Test
    void testFailureValidatorAndAnotherFailureValidator() {
        assertEquals(ValidationResultEnum.FAILURE1, failure1Validator.and(failure2Validator).validate(1));
        assertEquals(ValidationResultEnum.FAILURE2, failure2Validator.and(failure1Validator).validate(1));
    }

    @Test
    void testFailureAndSuccessValidator() {
        assertEquals(ValidationResultEnum.FAILURE1, successValidator.and(failure1Validator).validate(1));
        assertEquals(ValidationResultEnum.FAILURE1, failure1Validator.and(successValidator).validate(1));
    }

    @Test
    void testSuccessAndSuccessValidator() {
        assertEquals(ValidationResultEnum.SUCCESS, successValidator.and(successValidator).validate(1));
    }

}

enum ValidationResultEnum implements ValidationResult {
    FAILURE1, FAILURE2, SUCCESS;

    @Override
    public boolean isSuccess() {
        return this == SUCCESS;
    }
}