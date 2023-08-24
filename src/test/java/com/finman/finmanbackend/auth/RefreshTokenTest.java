package com.finman.finmanbackend.auth;

import org.junit.jupiter.api.Test;

import java.sql.Ref;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenTest {
    @Test
    void testIsNotExpiredIfExpired() {
        RefreshToken refreshToken = new RefreshToken(null, null, LocalDateTime.now().minus(5, ChronoUnit.MILLIS));
        assertFalse(refreshToken.isNotExpired());
    }

    @Test
    void testIfNotExpiredIfNotExpired() {
        RefreshToken refreshToken = new RefreshToken(null, null, LocalDateTime.now().plus(5, ChronoUnit.SECONDS));
        assertTrue(refreshToken.isNotExpired());
    }
}