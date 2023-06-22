package com.finman.finmanbackend.security.jwt;

import com.finman.finmanbackend.config.ApplicationProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * Used for creating JWT tokens and verifying them when authenticating the user.
 * Modifiable by tweaking the 'application.properties' file.
 *
 * @see ApplicationProperties
 * @author AdamBalski
 */
@Component
public class JwtUtil {
    private final JwtParser jwtParser;
    private final Key signingKey;
    private final long expirationPeriod;

    @Autowired
    public JwtUtil(ApplicationProperties applicationProperties) {
        ApplicationProperties.Security.Jwt jwtProperties = applicationProperties
                .getSecurity()
                .getJwt();
        this.expirationPeriod = jwtProperties.getExpiresIn();
        String signingKeyPlainText = jwtProperties.getSigningKey();
        this.signingKey = Keys.hmacShaKeyFor(signingKeyPlainText.getBytes(StandardCharsets.UTF_8));

        this.jwtParser = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build();
    }

    /**
     * Checks if the token is valid and returns the username it contains.
     *
     * @param token the JWT token (make sure to not include the 'Bearer ' part if it was included in the authentication header
     * @return the username of the authenticated user
     * @throws JwtException if the JWT is expired, malformed, expired, missing "sub" claim (username), signature or contains invalid signature
     * @throws IllegalArgumentException if the JWT is null or empty
     * @see JwtException
     */
    public String verifyAndExtractUsername(@NonNull String token) throws JwtException, IllegalArgumentException {
        Claims jwsClaims = jwtParser.parseClaimsJws(token).getBody();
        // Invalidate JWSs without expiration date
        if(jwsClaims.getExpiration() == null) {
            var header = jwtParser.parseClaimsJws(token).getHeader();
            throw new MissingClaimException(header, jwsClaims, "The JWT should contain the \"exp\" claim, but it does not.");
        }
        return jwsClaims.getSubject();
    }

    /**
     * Creates a JWS with "sub" claim equal to the username
     *
     * @param username used as the "sub" claim
     * @return the JWS token
     * @throws NullPointerException if the username is null
     * @throws IllegalArgumentException if the username is empty
     */
    public String createToken(@NonNull String username) throws IllegalArgumentException {
        if(username.length() == 0) {
            throw new IllegalArgumentException("Username should not be null and should be non-empty.");
        }
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(getExpirationTime()))
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    private long getExpirationTime() {
        return new Date().getTime() + 1000 * expirationPeriod;
    }
}
