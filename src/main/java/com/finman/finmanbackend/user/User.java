package com.finman.finmanbackend.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Models/symbolizes a person using the app.
 * Used for persistence with db.
 *
 * @see UserRole
 * @see Entity
 * @see Table
 * @author Adam Balski
 */
@Entity
@NoArgsConstructor @AllArgsConstructor
@Table(name = "app_user", schema = "public")
public class User {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Getter
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Getter
    @Column(name = "password", nullable = false)
    private String password;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role.getGrantedAuthority());
    }
}
