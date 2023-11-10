package com.finman.finmanbackend.auth;

import com.finman.finmanbackend.config.ApplicationProperties;
import com.finman.finmanbackend.security.jwt.JwtUtil;
import com.finman.finmanbackend.user.User;
import com.finman.finmanbackend.user.UserRepository;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static com.finman.finmanbackend.auth.HttpAuthExceptions.*;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final ApplicationProperties applicationProperties;

    @Autowired
    public AuthenticationService(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 RefreshTokenRepository refreshTokenRepository,
                                 JwtUtil jwtUtil,
                                 ApplicationProperties applicationProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtil = jwtUtil;
        this.applicationProperties = applicationProperties;
    }
    public Cookie authorizeAndCreateRefreshTokenCookie(LoginDto loginDto)
            throws  NoSuchUsername401,
                    WrongPassword401 {
        // no such username check
        User user = userRepository.findOneByEmail(loginDto.identifier())
                .orElseThrow(NoSuchUsername401::new);

        // wrong password check
        String raw = loginDto.password();
        String hashed = user.getPassword();
        if(!passwordEncoder.matches(raw, hashed)) {
            throw new WrongPassword401();
        }

        // jpa (hibernate) will overwrite the id
        // the repository's saveAndFlush method will return
        // a refresh token instance with the in-db saved uuid
        RefreshToken refreshToken = new RefreshToken(user, createRefreshTokenExpirationDate());
        UUID uuid = refreshTokenRepository.saveAndFlush(refreshToken).getUuid();

        Cookie cookie = new Cookie("REFRESH_TOKEN", uuid.toString());
        cookie.setMaxAge((int)applicationProperties.getSecurity().getRefreshToken().getExpiresIn());
        cookie.setPath("/api/v1/auth/get-jwt");
        return cookie;
    }

    private LocalDateTime createRefreshTokenExpirationDate() {
        long expiresIn = applicationProperties.getSecurity().getRefreshToken().getExpiresIn();
        return LocalDateTime.now()
                .plus(expiresIn, ChronoUnit.SECONDS);
    }

    public JwtDto refreshJwt(String refreshTokenId)
            throws  NoSuchRefreshToken401,
                    MalformedRefreshTokenId401 {
        UUID uuid;
        try {
            uuid = UUID.fromString(refreshTokenId);
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new MalformedRefreshTokenId401();
        }

        return refreshTokenRepository.findById(uuid)
                .filter(RefreshToken::isNotExpired)
                .map(RefreshToken::getUser)
                .map(com.finman.finmanbackend.user.User::getEmail)
                .map(jwtUtil::createToken)
                .map(JwtDto::new)
                .orElseThrow(NoSuchRefreshToken401::new);
    }
}
