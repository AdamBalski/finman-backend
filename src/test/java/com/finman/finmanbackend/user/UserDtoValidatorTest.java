package com.finman.finmanbackend.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.cassandra.DataCassandraTest;

import static com.finman.finmanbackend.user.UserDtoValidationResult.*;
import static org.junit.jupiter.api.Assertions.*;

class UserDtoValidatorTest {
    private UserDto userDto;

    @BeforeEach
    void init() {
        this.userDto = new UserDto("ema@il", "password");
    }

    @Test
    void testValidateWhenEmailNull() {
        userDto.setEmail(null);
        assertEquals(EMAIL_NULL, UserDtoValidator.validate(userDto));
    }

    @Test
    void testValidateWhenPasswordNull() {
        userDto.setPassword(null);
        assertEquals(PASSWORD_NULL, UserDtoValidator.validate(userDto));
    }

    @Test
    void testValidate() {
        assertEquals(VALID, UserDtoValidator.validate(userDto));
    }

    @Test
    void testValidateWhenPasswordTooShort() {
        userDto.setPassword("1234567");
        assertEquals(PASSWORD_TOO_SHORT, UserDtoValidator.validate(userDto));
    }

    @Test
    void testValidateWhenEmailTooLong() {
        userDto.setEmail("1".repeat(255)+ "@" + "lol.com");
        assertEquals(EMAIL_TOO_LONG, UserDtoValidator.validate(userDto));
    }

    @Test
    void testValidateWhenEmailNotEmail() {
        userDto.setEmail("blablabla");
        assertEquals(EMAIL_NOT_EMAIL, UserDtoValidator.validate(userDto));
    }

    @Test
    void testValidateWhenEmailContainsTwoAts() {
        userDto.setEmail("b@@lablabla");
        assertEquals(EMAIL_NOT_EMAIL, UserDtoValidator.validate(userDto));
    }
    @Test
    void testValidateWhenContainsTwoAtsSeparated() {
        userDto.setEmail("b@a@lablabla");
        assertEquals(EMAIL_NOT_EMAIL, UserDtoValidator.validate(userDto));
    }

    @Test
    void testValidateWhenEmailUsernameEmpty() {
        userDto.setEmail("@lablabla");
        assertEquals(EMAIL_NOT_EMAIL, UserDtoValidator.validate(userDto));
    }

    @Test
    void testValidateWhenHostnameEmpty() {
        userDto.setEmail("lablabla@");
        assertEquals(EMAIL_NOT_EMAIL, UserDtoValidator.validate(userDto));
    }

    @Test
    void testValidateWhenEmailIsAnAt() {
        userDto.setEmail("@");
        assertEquals(EMAIL_NOT_EMAIL, UserDtoValidator.validate(userDto));
    }

    @Test
    void testValidateWhenPasswordContainsInvalidCharacters() {
        userDto.setPassword("\\".repeat(8));
        assertEquals(PASSWORD_INVALID_CHARACTERS, UserDtoValidator.validate(userDto));
    }
}