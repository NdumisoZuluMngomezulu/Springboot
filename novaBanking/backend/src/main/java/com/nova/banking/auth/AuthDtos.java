package com.nova.banking.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;

public final class AuthDtos {
    private AuthDtos() {}
    public record RegisterRequest(
        @NotBlank @Size(max = 120) String fullName,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Email @Size(min = 12, max = 72) String password
    ) {}
    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}
    public record UserResponse(UUID id, String fullName, String email, Instant createdAt) {}
    public record AuthResponse(String tokenType, String token, long expiresInMs, UserResponse User) {}
}
