package com.finman.finmanbackend.auth;

import com.finman.finmanbackend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Used for persistence of the {@link RefreshTokenRepository} table in the db.
 *
 * @see JpaRepository
 * @see RefreshToken
 * @see User
 * @see UUID
 * @author AdamBalski
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> { }