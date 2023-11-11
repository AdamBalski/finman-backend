package com.finman.finmanbackend.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserRoleTest {
    @Test
    void testGetGrantedAuthority() {
        assertEquals("ROLE_ADMIN", UserRole.ADMIN.getGrantedAuthority().getAuthority());
        assertEquals("ROLE_STANDARD", UserRole.STANDARD.getGrantedAuthority().getAuthority());
    }
}