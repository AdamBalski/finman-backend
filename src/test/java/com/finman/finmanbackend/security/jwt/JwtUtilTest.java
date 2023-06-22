package com.finman.finmanbackend.security.jwt;

import com.finman.finmanbackend.config.ApplicationProperties;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.MissingClaimException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {
    JwtUtil jwtUtil;
    @BeforeEach
    void init() {
        var applicationProperties = new ApplicationProperties() {
            @Override
            public Security getSecurity() {
                return new Security() {
                    @Override
                    public Jwt getJwt() {
                        return new Jwt() {
                            @Override
                            public long getExpiresIn() {
                                return 1800;
                            }

                            @Override
                            public String getSigningKey() {
                                return "HELLO".repeat(103);
                            }
                        };
                    }
                };
            }
        };
        this.jwtUtil = new JwtUtil(applicationProperties) {

        };
    }

    @Test
    void testVerifyAndExtractUsernameWithNullToken() {
        @SuppressWarnings("DataFlowIssue")
        Executable executable = () -> jwtUtil.verifyAndExtractUsername(null);
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void testVerifyAndExtractUsernameWithEmptyToken() {
        Executable executable = () -> jwtUtil.verifyAndExtractUsername("");
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void testVerifyAndExtractUsernameWithNotAToken() {
        Executable executable = () -> jwtUtil.verifyAndExtractUsername("nonsense");
        assertThrows(MalformedJwtException.class, executable);
    }

    @Test
    void testVerifyAndExtractUsernameWithWrongSignature() {
        // Signed with "HELLO"
        Executable executable = () -> jwtUtil.verifyAndExtractUsername("eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJleHAiOjkyMjMzNzIwMzY4NTQ3NzZ9.6FVLUtq-I24RWsXeb3IpnOmUYZ7ul9Up1L_3ih-Ak5AqD-hGJ6zYYXFhVCPOXsniGlf0pW0zH5JWRKzya0tXFQ");
        assertThrows(SignatureException.class, executable);

    }

    @Test
    void testVerifyAndExtractUsernameWithNoExpirationTime() {
        Executable executable = () -> jwtUtil.verifyAndExtractUsername("eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJtYXJrIn0.mIkkZMMhfxLhRSMfkf-xB6QHmp178zWeIttZcCLPQccnUeLQ6v_hQLDXKFesYHuoUiRJHwSMLXh4JNYu7fVr4w");
        assertThrows(MissingClaimException.class, executable);
    }

    @Test
    void testVerifyAndExtractUsernameNoSubject() {
        String actual = jwtUtil.verifyAndExtractUsername("eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJleHAiOjIzMzcyMDM2ODU0Nzc1fQ.aonQPNN19GZApNQkaLdbi3ebOimNtuhzOzxSInS3KqzAzFfAMg87LjV8WhvFjQVes7GbnI_3RWAlTJLw6q0-8A");
        assertNull(actual);
    }

    @Test
    void testVerifyAndExtractUsernameWithNotSignedToken() {
        Executable executable = () -> jwtUtil.verifyAndExtractUsername("eyJ0eXAiOiJKV1QiLCJhbGciOiJub25lIn0.eyJzdWIiOiJzdWIiLCJleHAiOjExMTE1MTYyMzkwMjJ9");
        assertThrows(MalformedJwtException.class, executable);
    }

    @Test
    void testVerifyAndExtractUsername() {
        String actual = jwtUtil.verifyAndExtractUsername("eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJleHAiOjIzMzcyMDM2ODU0Nzc1LCJzdWIiOiJ1c2VybmFtZSJ9.TdCBLzgl9Q6yg56FNx9u6On4KgICkpzLpm9GImUXd8BcEJz4SkpxQgMEvBmKzXnGJazwPu18S9pJmm4vpIgJbw");
        assertEquals("username", actual);
    }

    @Test
    void testCreateTokenWithNullUsername() {
        @SuppressWarnings("DataFlowIssue")
        Executable executable = () -> jwtUtil.createToken(null);
        assertThrows(NullPointerException.class, executable);
    }

    @Test
    void testCreateTokenWithEmptyUsername() {
        Executable executable = () -> jwtUtil.createToken("");
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void testCreateToken() {
        String token = jwtUtil.createToken("rafa");
        // gets first two parts without the dot (header json + claims json)
        String tokenFirstTwoParts = token.replaceAll("[^.]*$", "").replaceAll("[.]", "");
        String decoded = new String(Base64.getDecoder().decode(tokenFirstTwoParts), StandardCharsets.UTF_8);
        assertTrue(decoded.contains("rafa"));
        assertTrue(decoded.contains("HS512"));
        assertTrue(decoded.contains("exp"));
        assertTrue(decoded.contains("sub"));

        // get exp number
        int start = decoded.indexOf("exp");
        // 5 skips past exp",
        String startsWithExpValue = decoded.substring(start + 5);
        int indexOfClosingBrace = startsWithExpValue.indexOf("}");
        long expValue = Long.parseLong(startsWithExpValue.substring(0, indexOfClosingBrace));
        // checks exp is not in the past
        assertTrue(expValue > new Date().getTime() / 1000);
        long diff = expValue - new Date().getTime() / 1000;
        assertTrue(diff > 1700);
        assertTrue(diff < 1900);



    }

    @Test
    void testApiIsSelfCompliant() {
        String username = "username";
        assertEquals(username, jwtUtil.verifyAndExtractUsername(jwtUtil.createToken(username)));
    }
}