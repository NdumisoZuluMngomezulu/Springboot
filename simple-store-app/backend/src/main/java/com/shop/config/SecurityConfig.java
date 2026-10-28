package com.shop.config;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Public: browse products, place an order.
 * ADMIN (HTTP Basic): create/update/delete products, list orders.
 * Everything else is denied. Stateless: no session, no cookies, so no CSRF surface.
 */
@Configuration
public class SecurityConfig {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    SecurityFilterChain api(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())   // uses the mappings in WebConfig; answers preflight before auth
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a
                .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/*").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/orders").permitAll()
                .requestMatchers("/api/products/**", "/api/orders/**").hasRole("ADMIN")
                .requestMatchers("/error").permitAll()
                .anyRequest().denyAll())
            // Custom entry point: JSON 401 and no WWW-Authenticate header, so browsers never pop up a login
            // dialog or cache credentials. Clients send the Authorization header themselves.
            .httpBasic(b -> b.authenticationEntryPoint((req, res, ex) -> json(res, 401, "Authentication required.")))
            .exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, ex) -> json(res, 401, "Authentication required."))
                .accessDeniedHandler((req, res, ex) -> json(res, 403, "Forbidden.")));
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    UserDetailsService users(PasswordEncoder encoder,
                             @Value("${shop.admin.username:admin}") String username,
                             @Value("${shop.admin.password:}") String password) {
        if (password.isBlank()) {
            byte[] raw = new byte[24];
            new SecureRandom().nextBytes(raw);
            password = Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
            log.warn("SHOP_ADMIN_PASSWORD is not set. Generated admin credentials for this run: {} / {}", username, password);
        }
        return new InMemoryUserDetailsManager(User.withUsername(username).password(encoder.encode(password)).roles("ADMIN").build());
    }

    private static void json(HttpServletResponse res, int status, String message) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}

// package com.shop.config;

// import jakarta.servlet.http.HttpServletResponse;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpMethod;
// import org.springframework.security.config.Customizer;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.core.userdetails.User;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.provisioning.InMemoryUserDetailsManager;
// import org.springframework.security.web.SecurityFilterChain;
// import java.io.IOException;
// import java.security.SecureRandom;
// import java.util.Base64;

// /**
//  * Public: browse products, place an order.
//  * ADMIN (HTTP Basic): create/update/delete products, list orders.
//  * Everything else is denied. Stateless: no session, no cookies, so no CSRF surface.
//  */
// @Configuration
// public class SecurityConfig {
//     private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

//     @Bean
//     SecurityFilterChain api(HttpSecurity http) throws Exception {
//         http
//             .cors(Customizer.withDefaults())   // uses the mappings in WebConfig; answers preflight before auth
//             .csrf(csrf -> csrf.disable())
//             .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//             .authorizeHttpRequests(a -> a
//                 .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/*").permitAll()
//                 .requestMatchers(HttpMethod.POST, "/api/orders").permitAll()
//                 .requestMatchers("/api/products/**", "/api/orders/**").hasRole("ADMIN")
//                 .requestMatchers("/error").permitAll()
//                 .anyRequest().denyAll())
//             // Custom entry point: JSON 401 and no WWW-Authenticate header, so browsers never pop up a login
//             // dialog or cache credentials. Clients send the Authorization header themselves.
//             .httpBasic(b -> b.authenticationEntryPoint((req, res, ex) -> json(res, 401, "Authentication required.")))
//             .exceptionHandling(e -> e
//                 .authenticationEntryPoint((req, res, ex) -> json(res, 401, "Authentication required."))
//                 .accessDeniedHandler((req, res, ex) -> json(res, 403, "Forbidden.")));
//         return http.build();
//     }

//     @Bean
//     PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

//     @Bean
//     UserDetailsService users(PasswordEncoder encoder,
//                              @Value("${shop.admin.username:admin}") String username,
//                              @Value("${shop.admin.password:}") String password) {
//         if (password.isBlank()) {
//             byte[] raw = new byte[24];
//             new SecureRandom().nextBytes(raw);
//             password = Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
//             log.warn("SHOP_ADMIN_PASSWORD is not set. Generated admin credentials for this run: {} / {}", username, password);
//         }
//         return new InMemoryUserDetailsManager(User.withUsername(username).password(encoder.encode(password)).roles("ADMIN").build());
//     }

//     private static void json(HttpServletResponse res, int status, String message) throws IOException {
//         res.setStatus(status);
//         res.setContentType("application/json");
//         res.setCharacterEncoding("UTF-8");
//         res.getWriter().write("{\"error\":\"" + message + "\"}");
//     }
// }

