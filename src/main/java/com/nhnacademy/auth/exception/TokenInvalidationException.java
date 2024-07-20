package com.nhnacademy.auth.exception;

public class TokenInvalidationException extends RuntimeException {
    public TokenInvalidationException(String message) {
        super(message);
    }
}
