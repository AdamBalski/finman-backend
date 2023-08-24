package com.finman.finmanbackend.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncoderFactoryTest {
    @Test
    void testPasswordEncoder() {
        PasswordEncoder passwordEncoder = new PasswordEncoderFactory().passwordEncoder();
        assertInstanceOf(BCryptPasswordEncoder.class, passwordEncoder);

        // Pretty much guarantees that strength > 0
        assertNotEquals("password", passwordEncoder.encode("password"));
    }
}