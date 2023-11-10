package com.finman.finmanbackend.user;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.finman.finmanbackend.user.UserDtoValidationResult.*;
import static org.junit.jupiter.api.Assertions.*;

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