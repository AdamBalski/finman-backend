package com.finman.finmanbackend.auth;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class HttpAuthExceptionsTest {
    @Test
    void testNoSuchUsernameException() {
        assertEquals(HttpStatus.UNAUTHORIZED, new HttpAuthExceptions.NoSuchUsername401().getStatusCode());
        assertEquals("NO_SUCH_USERNAME", new HttpAuthExceptions.NoSuchUsername401().getReason());
    }

    @Test
    void testWrongPasswordException() {
        assertEquals(HttpStatus.UNAUTHORIZED, new HttpAuthExceptions.WrongPassword401().getStatusCode());
        assertEquals("WRONG_PASSWORD", new HttpAuthExceptions.WrongPassword401().getReason());
    }

    @Test
    void testNoSuchRefreshTokenException() {
        assertEquals(HttpStatus.UNAUTHORIZED, new HttpAuthExceptions.NoSuchRefreshToken401().getStatusCode());
        assertEquals("NO_SUCH_REFRESH_TOKEN", new HttpAuthExceptions.NoSuchRefreshToken401().getReason());
    }

    @Test
    void testMalformedRefreshToken() {
        assertEquals(HttpStatus.UNAUTHORIZED, new HttpAuthExceptions.MalformedRefreshTokenId401().getStatusCode());
        assertEquals("MALFORMED_REFRESH_TOKEN", new HttpAuthExceptions.MalformedRefreshTokenId401().getReason());
    }
}