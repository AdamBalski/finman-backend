package com.finman.finmanbackend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Maps 'application.*' properties from the src/{main or test}/resources/application.properties
 *
 * @author AdamBalski
 */
@Configuration
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {
    @Getter
    @Setter
    Security security;
    public static class Security {
        @Getter
        @Setter
        Jwt jwt;
        public static class Jwt {
            @Getter
            @Setter
            private long expiresIn;
            @Getter
            @Setter
            private String signingKey;
        }
    }

}
