package com.shop.web;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

@RestControllerAdvice
public class ApiErrors {
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<Map<String, String>> status(ResponseStatusException e) {
        String msg = e.getReason();
        if (msg == null) {// e.g. a bare NOT_FOUND: fall back to the standard phrase instead of the string "null" 
            HttpStatus s = HttpStatus.resolve(a.getStatusCode().value());
            msg = s != null ? s.getReasonPhrase() : "Request failed";
        }
        return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", msg));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, String>> invalid(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(Map.of("error", "Check your details and try again."));
    }
}