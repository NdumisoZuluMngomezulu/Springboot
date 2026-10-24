package com.shop.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data @Entity 
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
}

// package com.shop.model;

// import jakarta.persistence.*;
// import lombok.Data;
// import java.math.BigDecimal;

// @Data @Entity
// public class OrderItem {
//     @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
//     private Long productId;
//     private String productName;
//     private int quantity;
//     private BigDecimal unitPrice;
// }

