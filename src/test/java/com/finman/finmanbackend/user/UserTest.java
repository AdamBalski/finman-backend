package com.finman.finmanbackend.user;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {
    @Test
    void testGetAuthorities() {
        Collection<? extends GrantedAuthority> authorities = new User(
                null,
                null,
                null,
                UserRole.STANDARD
        ).getAuthorities();
        assertEquals(1, authorities.size());
        assertEquals("ROLE_STANDARD", authorities.iterator().next().getAuthority());
    }
}