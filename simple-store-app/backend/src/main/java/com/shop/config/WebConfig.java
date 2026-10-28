package com.shop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override public void addCorsMappings(CorsRegistry r) {
        r.addMapping("/api/**").allowedOrigins("http://localhost:5500", "http://127.0.0.1:5500", "http://localhost:3000")
         .allowedMethods("GET", "POST", "PUT", "DELETE")
         .allowedHeaders("Content-Type", "Authorization", "Idempotency-Key")
         .exposedHeaders("Idempotent-Replayed");
    }
}


// package com.shop.config;

// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.servlet.config.annotation.*;

// @Configuration
// public class WebConfig implements WebMvcConfigurer {
//     @Override public void addCorsMappings(CorsRegistry r) {
//         r.addMapping("/api/**").allowedOrigins("http://localhost:5500", "http://127.0.0.1:5500", "http://localhost:3000")
//          .allowedMethods("GET", "POST", "PUT", "DELETE")
//          .allowedHeaders("Content-Type", "Authorization", "Idempotency-Key")
//          .exposedHeaders("Idempotent-Replayed");
//     }
// }
