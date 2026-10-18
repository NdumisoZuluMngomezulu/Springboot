package com.nova.banking.account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

import javax.annotation.processing.Generated;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor 
public class User{
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, length = 254)
    private String email;

    @Column(nullable = false, length = 100)
    private String passwordHash;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate(){
        createdAt = Instant.now();
        email = email == null ? null : email.trim(.toLowerCase());
    }
}