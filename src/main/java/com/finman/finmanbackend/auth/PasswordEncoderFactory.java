package com.finman.finmanbackend.auth;

import com.finman.finmanbackend.user.User;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Used for hashing passwords and validating passwords, provided by users for authentication, against the db-stored hashed passwords.
 *
 * @see User
 * @see UUID
 * @author AdamBalski
 */
@Component
public class PasswordEncoderFactory {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
