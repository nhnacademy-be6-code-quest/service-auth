package com.nhnacademy.auth.exception;

public class DeletedClientException extends RuntimeException {
    public DeletedClientException(String message) {
        super(message);
    }
}
