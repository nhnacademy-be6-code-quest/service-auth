package com.nhnacademy.auth.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UnknownRoleExceptionTest {

    @Test
    void testExceptionMessage() {
        String errorMessage = "Invalid role: ROLE_UNKNOWN";
        UnknownRoleException exception = new UnknownRoleException(errorMessage);

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    void testThrowException() {
        String errorMessage = "Invalid role: ROLE_UNKNOWN";

        assertThatThrownBy(() -> {
            throw new UnknownRoleException(errorMessage);
        }).isInstanceOf(UnknownRoleException.class)
                .hasMessage(errorMessage);
    }
}
