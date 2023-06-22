package com.finman.finmanbackend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Used for persistence of the {@link User} table in the db.
 *
 * @see JpaRepository
 * @see User
 * @see UUID
 * @author AdamBalski
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findOneByEmail(String email);
}
