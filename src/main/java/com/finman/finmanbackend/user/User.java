package com.finman.finmanbackend.user;

import com.finman.finmanbackend.account.Account;
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
@Getter
@Entity
@NoArgsConstructor @AllArgsConstructor
@Table(name = "app_user", schema = "public")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role.getGrantedAuthority());
    }
}
