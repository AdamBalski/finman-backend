package com.finman.finmanbackend.action;

import com.finman.finmanbackend.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "action")
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "product_id", columnDefinition = "uuid", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private double quantity;

    @Column(name = "datetime", nullable = false)
    private LocalDateTime datetime;

    @Column(name = "note")
    private String note;

    @Column(name = "price_in_cents", nullable = false)
    private long priceInCents;
}