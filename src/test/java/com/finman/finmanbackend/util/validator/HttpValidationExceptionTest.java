package com.finman.finmanbackend.util.validator;

import com.finman.finmanbackend.user.UserDto;
import com.finman.finmanbackend.user.UserDtoValidationResult;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpValidationExceptionTest {
    @Test
    void testExceptionWithUserDto() {
        assertEquals("com.finman.finmanbackend.user.UserDto_INVALID", new HttpValidationException(UserDto.class).getReason());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, new HttpValidationException(UserDto.class).getStatusCode());
    }

    @Test
    void testExceptionWithUserDtoHavingTooShortOfAPassword() {
        ValidationResult validationResult = UserDtoValidationResult.PASSWORD_TOO_SHORT;
        HttpValidationException exception = new HttpValidationException(validationResult, UserDto.class);
        assertEquals("com.finman.finmanbackend.user.UserDto_INVALID_PASSWORD_TOO_SHORT", exception.getReason());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, new HttpValidationException(UserDto.class).getStatusCode());
    }

    @Test
    void testExceptionWithEntityString() {
        ResponseStatusException exception = new HttpValidationException("reason");
        assertEquals("reason_INVALID", exception.getReason());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getStatusCode());
    }
}