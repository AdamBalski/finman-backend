package com.finman.finmanbackend.auth;

import com.finman.finmanbackend.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Models/symbolizes a refresh token of a user.
 * Used as an authentication mechanism. <br>
 *
 * There can be multiple refresh tokens for one user. <br>
 * Users can fetch refresh tokens by providing their credentials. <br>
 * Users with active refresh tokens can fetch SWTs, which are used for direct authentication.
 *
 * @see RefreshToken
 * @see User
 * @see Entity
 * @see Table
 * @author Adam Balski
 */
@NoArgsConstructor @AllArgsConstructor
@Getter
@Entity
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID uuid;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(name = "exp", nullable = false)
    private LocalDateTime expiryDate;

    public RefreshToken(User user, LocalDateTime expiryDate) {
        this.uuid = UUID.randomUUID();
        this.user = user;
        this.expiryDate = expiryDate;
    }

    public boolean isNotExpired() {
        return !LocalDateTime.now().isAfter(expiryDate);
    }
}