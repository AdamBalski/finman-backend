package com.finman.finmanbackend.user;

import com.finman.finmanbackend.util.validator.HttpValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void init() {
        userRepository = Mockito.mock(UserRepository.class);
        var noopPasswordEncoder = new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return Objects.equals(encode(rawPassword), encodedPassword);
            }
        };
        userService = new UserService(userRepository, noopPasswordEncoder);
    }

    @Test
    void testGetByIdIfUserDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        Executable executable = () -> userService.getById(id);
        assertThrows(HttpUserExceptions.IdNotFound404.class, executable);

        verify(userRepository).findById(id);
    }

    @Test
    void testGetByIdIfUserDoesExist() {
        UUID uuid = UUID.randomUUID();
        User user = Mockito.mock(User.class);
        when(userRepository.findById(uuid))
                .thenReturn(Optional.of(user));

        assertEquals(user, userService.getById(uuid));

        verify(userRepository).findById(uuid);
    }

    @Test
    void testPutWithTooShortPassword() {
        UserDto userDto = new UserDto("email@email.com", "short");
        Executable executable = () -> userService.put(userDto);
        HttpValidationException exception = assertThrows(HttpValidationException.class, executable);
        assertEquals("com.finman.finmanbackend.user.UserDto_INVALID_PASSWORD_TOO_SHORT", exception.getReason());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getStatusCode());
        verifyNoInteractions(userRepository);
    }

    @Test
    void testPutWhenEmailTaken() {
        UserDto userDto = new UserDto("email@email.com", "password");
        when(userRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);
        Executable executable = () -> userService.put(userDto);

        HttpUserExceptions.EmailTaken409 exception = assertThrows(HttpUserExceptions.EmailTaken409.class, executable);
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(userRepository).save(any());
    }

    @Test
    void testPut() {
        UserDto userDto = new UserDto("email@email.com", "password");
        when(userRepository.save(any(User.class))).thenReturn(null);
        Executable executable = () -> userService.put(userDto);

        assertDoesNotThrow(executable);
        verify(userRepository).save(any());
    }
}