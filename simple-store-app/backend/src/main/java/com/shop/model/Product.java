package com.shop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data @Entity
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @NotBlank private String name;

    @Column(length= 1000) private String description;

    @NotNull @DecimalMin("0.0") private BigDecimal price;
    
    @Min(0) private int stock;

    private String category;
    
    @Pattern(regexp = "^(https?://.+)?)", message = "imageUrl must be http(s)") private String imageUrl;
}

// package com.shop.model;

// import jakarta.persistence.*;
// import jakarta.validation.constraints.*;
// import lombok.Data;
// import java.math.BigDecimal;

// @Data @Entity
// public class Product {
//     @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
//     @NotBlank private String name;
//     @Column(length = 1000) private String description;
//     @NotNull @DecimalMin("0.0") private BigDecimal price;
//     @Min(0) private int stock;
//     private String category;
//     @Pattern(regexp = "^(https?://.+)?$", message = "imageUrl must be http(s)") private String imageUrl;
// }