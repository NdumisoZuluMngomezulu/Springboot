package com.example.banking.common;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.MethodArgumentNotValidException;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler (ApiException.class)
    ResponseEntity<?> handleApi(ApiException ex) {
        return response(ex.getStatus(), exMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex)
}
