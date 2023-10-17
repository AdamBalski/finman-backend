package com.finman.finmanbackend.account;

import com.finman.finmanbackend.user.User;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "account", schema = "public")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, nullable = false, columnDefinition = "uuid")
    UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "uuid")
    private User user;

    @Column(name = "name", nullable = false)
    private String name;
}
