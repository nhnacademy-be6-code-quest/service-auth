package com.nhnacademy.auth.dto.request;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;

class ClientLoginRequestDtoTest {

    @Test
    void testNoArgsConstructor() {
        ClientLoginRequestDto dto = new ClientLoginRequestDto();
        assertThat(dto.getClientEmail()).isNull();
        assertThat(dto.getClientPassword()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        ClientLoginRequestDto dto = new ClientLoginRequestDto("test@example.com", "password");
        assertThat(dto.getClientEmail()).isEqualTo("test@example.com");
        assertThat(dto.getClientPassword()).isEqualTo("password");
    }

    @Test
    void testSettersAndGetters() {
        ClientLoginRequestDto dto = new ClientLoginRequestDto();
        dto.setClientEmail("test@example.com");
        dto.setClientPassword("password");

        assertThat(dto.getClientEmail()).isEqualTo("test@example.com");
        assertThat(dto.getClientPassword()).isEqualTo("password");
    }

    @Test
    void testToString() {
        ClientLoginRequestDto dto = new ClientLoginRequestDto("test@example.com", "password");
        assertThat(dto.toString()).hasToString("ClientLoginRequestDto(clientEmail=test@example.com, clientPassword=password)");
    }

    @Test
    void testEqualsAndHashCode() {
        ClientLoginRequestDto dto1 = new ClientLoginRequestDto("test@example.com", "password");
        ClientLoginRequestDto dto2 = new ClientLoginRequestDto("test@example.com", "password");
        ClientLoginRequestDto dto3 = new ClientLoginRequestDto("other@example.com", "password");

        assertThat(dto1).isEqualTo(dto2).isNotEqualTo(dto3);
        assertThat(dto1.hashCode()).hasSameHashCodeAs(dto2.hashCode());
        assertThat(dto1.hashCode()).isNotEqualTo(dto3.hashCode());
    }
}
