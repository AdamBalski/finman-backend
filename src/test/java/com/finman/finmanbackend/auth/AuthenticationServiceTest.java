package com.finman.finmanbackend.auth;

import com.finman.finmanbackend.config.ApplicationProperties;
import com.finman.finmanbackend.security.jwt.JwtUtil;
import com.finman.finmanbackend.user.User;
import com.finman.finmanbackend.user.UserRepository;
import com.finman.finmanbackend.user.UserRole;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

class AuthenticationServiceTest {
    private UserRepository mockUserRepository;
    private PasswordEncoder mockPasswordEncoder;
    private RefreshTokenRepository mockRefreshTokenRepository;
    private JwtUtil mockJwtUtil;

    private AuthenticationService authenticationService;

    @BeforeEach
    void init() {
        mockUserRepository = Mockito.mock(UserRepository.class);
        mockPasswordEncoder = Mockito.mock(PasswordEncoder.class);
        mockRefreshTokenRepository = Mockito.mock(RefreshTokenRepository.class);
        mockJwtUtil = Mockito.mock(JwtUtil.class);

        // set up application properties
        ApplicationProperties mockApplicationProperties = new ApplicationProperties();
        var securityProperties = new ApplicationProperties.Security();
        var refreshTokenProperties = new ApplicationProperties.Security.RefreshToken();
        refreshTokenProperties.setExpiresIn(42);
        securityProperties.setRefreshToken(refreshTokenProperties);
        mockApplicationProperties.setSecurity(securityProperties);

        authenticationService = new AuthenticationService(
                mockUserRepository,
                mockPasswordEncoder,
                mockRefreshTokenRepository,
                mockJwtUtil,
                mockApplicationProperties
        );
    }

    @Test
    void testRefreshJwtWhenRefreshTokenNonexistent() {
        UUID uuid = UUID.randomUUID();

        Mockito.when(mockRefreshTokenRepository.findById(uuid)).thenReturn(Optional.empty());
        Executable executable = () -> authenticationService.refreshJwt(uuid.toString());

        assertThrows(HttpAuthExceptions.NoSuchRefreshToken401.class, executable);
    }

    @Test
    void testRefreshJwtWithNullRefreshTokenId() {
        Executable executable = () -> authenticationService.refreshJwt(null);
        assertThrows(HttpAuthExceptions.MalformedRefreshTokenId401.class, executable);
    }

    @Test
    void testRefreshJwtWithMalformedRefreshTokenId() {
        Executable executable = () -> authenticationService.refreshJwt("not a uuid bozo");
        assertThrows(HttpAuthExceptions.MalformedRefreshTokenId401.class, executable);
    }

    @Test
    void testRefreshJwtWithExpiredJwt() {
        UUID uuid = UUID.randomUUID();
        LocalDateTime expired = LocalDateTime
                .now()
                .minus(1, ChronoUnit.YEARS);
        User user = null;
        RefreshToken refreshToken = new RefreshToken(uuid, user, expired);

        Mockito.when(mockRefreshTokenRepository.findById(uuid)).thenReturn(Optional.of(refreshToken));

        Executable executable = () -> authenticationService.refreshJwt(uuid.toString());
        assertThrows(HttpAuthExceptions.NoSuchRefreshToken401.class, executable);
    }

    @Test
    void testRefreshJwt() {
        UUID uuid = UUID.randomUUID();
        LocalDateTime notExpired = LocalDateTime
                .now()
                .plus(1, ChronoUnit.YEARS);
        User user = new User(UUID.randomUUID(), "email", "pass", UserRole.STANDARD);
        RefreshToken refreshToken = new RefreshToken(uuid, user, notExpired);

        Mockito.when(mockRefreshTokenRepository.findById(uuid)).thenReturn(Optional.of(refreshToken));
        Mockito.when(mockJwtUtil.createToken("email")).thenReturn("jwt");

        JwtDto expected = new JwtDto("jwt");
        assertEquals(expected, authenticationService.refreshJwt(uuid.toString()));
    }

    @Test
    void testAuthorizeAndCreateRefreshTokenCookie() {
        LoginDto loginDto = new LoginDto("email", "nooppass");

        // Set up mockUserRepository
        UUID uuid = UUID.randomUUID();
        User user = new User(uuid, "email", "nooppass", UserRole.ADMIN);
        Mockito.when(mockUserRepository.findOneByEmail("email")).thenReturn(Optional.of(user));

        // Set up refreshTokenRepository
        UUID saveUuid = UUID.randomUUID();
        RefreshToken savedRefreshToken = new RefreshToken(saveUuid, user, LocalDateTime.MAX);
        Mockito.when(mockRefreshTokenRepository.saveAndFlush(any(RefreshToken.class))).thenReturn(savedRefreshToken);

        // Set up noop password encoder
        Mockito.when(mockPasswordEncoder.matches("nooppass", "nooppass")).thenReturn(true);

        Cookie result = authenticationService.authorizeAndCreateRefreshTokenCookie(loginDto);

        // check if returned id is same as the one saved in db
        var refreshTokenArgumentCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        Mockito.verify(mockRefreshTokenRepository).saveAndFlush(refreshTokenArgumentCaptor.capture());
        RefreshToken savedToken = refreshTokenArgumentCaptor.getValue();
        assertEquals(user, savedToken.getUser());
        assertTrue(savedToken.isNotExpired());

        assertEquals(result.getValue(), savedRefreshToken.getUuid().toString());
        assertEquals(result.getMaxAge(), 42);
        assertEquals(result.getPath(), "/api/v1/auth/get-jwt");
        assertEquals("REFRESH_TOKEN", result.getName());
    }

    @Test
    void testAuthorizeAndCreateRefreshTokenCookieIfThereIsNoSuchEmail() {
        LoginDto loginDto = new LoginDto("nonexistent-user", "nonexistent");

        Mockito.when(mockUserRepository.findOneByEmail("nonexistent-user")).thenReturn(Optional.empty());

        Executable executable = () -> authenticationService.authorizeAndCreateRefreshTokenCookie(loginDto);
        assertThrows(HttpAuthExceptions.NoSuchUsername401.class, executable);
    }

    @Test
    void testAuthorizeAndCreateRefreshTokenWhenPasswordsDontMatch() {
        LoginDto loginDto = new LoginDto("user", "password-wrong");

        UUID userId = UUID.randomUUID();
        User user = new User(userId, "email", "nooppass", UserRole.ADMIN);
        Mockito.when(mockUserRepository.findOneByEmail("user")).thenReturn(Optional.of(user));

        Mockito.when(mockPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);
        Mockito.when(mockPasswordEncoder.matches(eq("password-wrong"), anyString())).thenReturn(false);

        Executable executable = () -> authenticationService.authorizeAndCreateRefreshTokenCookie(loginDto);
        assertThrows(HttpAuthExceptions.WrongPassword401.class, executable);
    }
}