package com.finman.finmanbackend.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {
    private UserDetailsServiceImpl userDetailsService;
    private UserRepository userRepositoryMock;

    @BeforeEach
    void init() {
        userRepositoryMock = Mockito.mock(UserRepository.class);
        userDetailsService = new UserDetailsServiceImpl(userRepositoryMock);
    }

    @Test
    void testLoadUserByUsername() {
        var user = new com.finman.finmanbackend.user.User(UUID.randomUUID(),
                "email123",
                "password123",
                UserRole.STANDARD);
        when(userRepositoryMock.findOneByEmail("email123")).thenReturn(Optional.of(user));

        assertEquals("password123", userDetailsService.loadUserByUsername("email123").getPassword());

        verify(userRepositoryMock, times(1)).findOneByEmail("email123");
        // assert there were no more different invocations
        verify(userRepositoryMock, never()).findOneByEmail(argThat(arg -> !arg.equals("email123")));
    }

    @Test
    void testLoadUserByUsernameIfNoSuchUser() {
        when(userRepositoryMock.findOneByEmail("email123")).thenReturn(Optional.empty());

        Executable executable = () -> userDetailsService.loadUserByUsername("email123");
        assertThrows(UsernameNotFoundException.class, executable);

        verify(userRepositoryMock, times(1)).findOneByEmail("email123");
        // assert there were no more different invocations
        verify(userRepositoryMock, never()).findOneByEmail(argThat(arg -> !arg.equals("email123")));
    }
}