package com.example.banking.common;

import com.example.banking.user.User;
import com.example.banking.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {
    private final UserRepository users;

    public CurrentUser(UserRepository users) {this.users = users;}

    public User require(Authentication authentication) {
        if (authentication == null || authentication.getName() == null){
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        return users.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User does not exists"));
    }
}