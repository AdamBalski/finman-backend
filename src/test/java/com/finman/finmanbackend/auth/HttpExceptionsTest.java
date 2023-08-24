package com.finman.finmanbackend.auth;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class HttpExceptionsTest {
    @Test
    void testNoSuchUsernameException() {
        assertEquals(HttpStatus.UNAUTHORIZED, new HttpExceptions.NoSuchUsername401().getStatusCode());
    }

    @Test
    void testWrongPasswordException() {
        assertEquals(HttpStatus.UNAUTHORIZED, new HttpExceptions.WrongPassword401().getStatusCode());
    }

    @Test
    void testNoSuchRefreshTokenException() {
        assertEquals(HttpStatus.UNAUTHORIZED, new HttpExceptions.NoSuchRefreshToken401().getStatusCode());
    }
}