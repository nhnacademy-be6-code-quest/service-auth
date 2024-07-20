package com.nhnacademy.auth.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TokenInvalidationExceptionTest {

    @Test
    void testExceptionMessage() {
        String errorMessage = "Token is invalid";

        TokenInvalidationException exception = assertThrows(TokenInvalidationException.class, () -> {
            throw new TokenInvalidationException(errorMessage);
        });

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }
}
