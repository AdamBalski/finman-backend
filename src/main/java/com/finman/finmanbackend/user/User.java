package com.finman.finmanbackend.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

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
@Setter
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

    @JsonIgnore // don't include in http responses
    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @JsonIgnore // don't include in http responses
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role.getGrantedAuthority());
    }

    public static User valueOf(UserDto userDto, PasswordEncoder passwordEncoder) {
        return new User(null, userDto.getEmail(), passwordEncoder.encode(userDto.getPassword()), UserRole.STANDARD);
    }
}
