package com.nhnacademy.auth.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoginFailExceptionTest {

    @Test
    void testExceptionMessage() {
        String errorMessage = "Login failed";

        LoginFailException exception = assertThrows(LoginFailException.class, () -> {
            throw new LoginFailException(errorMessage);
        });

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }
}
