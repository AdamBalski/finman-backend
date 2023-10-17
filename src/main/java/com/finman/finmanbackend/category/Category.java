package com.finman.finmanbackend.category;

import com.finman.finmanbackend.account.Account;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true, columnDefinition = "uuid")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "account_id", columnDefinition = "uuid", nullable = false)
    private Account account;

    @JoinColumn(name = "parent_category_id", columnDefinition = "uuid")
    @ManyToOne
    private Category parentCategory;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

}