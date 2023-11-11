package com.finman.finmanbackend.user;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.finman.finmanbackend.user.UserDtoValidationResult.VALID;
import static com.finman.finmanbackend.user.UserDtoValidationResult.values;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDtoValidationResultTest {
    @Test
    void testIsSuccess() {
        assertTrue(VALID.isSuccess());
        assertTrue(Arrays.stream(values())
                .filter(res -> res != VALID)
                .noneMatch(UserDtoValidationResult::isSuccess)
        );
    }

    @Test
    void testIsFailure() {
        assertFalse(VALID.isFailure());
        assertTrue(Arrays.stream(values())
                .filter(res -> res != VALID)
                .allMatch(UserDtoValidationResult::isFailure)
        );
    }

}