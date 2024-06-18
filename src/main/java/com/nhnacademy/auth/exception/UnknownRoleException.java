package com.nhnacademy.auth.exception;

public class UnknownRoleException extends RuntimeException {
    public UnknownRoleException(String message) {
        super(message);
    }
}
