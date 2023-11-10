package com.finman.finmanbackend.user;

import com.finman.finmanbackend.auth.HttpAuthExceptions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class HttpUserExceptionsTest {
    @Test
    void testEmailTakenException() {
        assertEquals(HttpStatus.CONFLICT, new HttpUserExceptions.EmailTaken409().getStatusCode());
        assertEquals("EMAIL_TAKEN", new HttpUserExceptions.EmailTaken409().getReason());
    }

    @Test
    void testIdNotFound() {
        assertEquals(HttpStatus.NOT_FOUND, new HttpUserExceptions.IdNotFound404().getStatusCode());
        assertEquals("ID_NOT_FOUND", new HttpUserExceptions.IdNotFound404().getReason());
    }
}