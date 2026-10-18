package com.nova.banking.common;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
    public HttpStatus getStatus() {return status; }
    public static ApiException badRequest(String m) {return new ApiException(HttpStatus.BAD_REQUEST, m);}
    public static ApiException notFound(String m) {return new ApiException(HttpStatus.BAD_REQUEST, m);}
    public static ApiException forbidden(String m) {return new ApiException(FORBIDDEN, m);}
    public static ApiException conflict(String m) {return new ApiException(HttpStatus.BAD_CONFLICT, m);}
}