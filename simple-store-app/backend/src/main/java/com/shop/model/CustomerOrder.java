package com.shop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import lombok.Data;

@Data @Entity @Table(name = "orders")
public class CustomerOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String customerName;
    private String email;
    private String address;
    private BigDecimal total;

    /**Client supplied idemptotency key, unique, one order per key in database */
    @JsonIgnore @Column(unique = true, length = 64) private String idempotencyKey;
    /**SHA-256 of normalized request, so a reused key with a different payload is detected.*/
    @JsonIgnore @Column(unique =  true, length = 64) private String requestHash;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(cascade = CascadeType.ALL) @JoinColumn(name = "order_id")
    private List<OrderItem> items = new ArrayList<>();
}

// @JsonIgnore @Column(unique = true, length = 64) private String idempotencyKey;
//     /** SHA-256 of the normalised request, so a reused key with a different payload is detected. */
//     @JsonIgnore @Column(length = 64) private String requestHash;
//     private LocalDateTime createdAt = LocalDateTime.now();
//     @OneToMany(cascade = CascadeType.ALL) @JoinColumn(name = "order_id")
//     private List<OrderItem> items = new ArrayList<>();
// }

