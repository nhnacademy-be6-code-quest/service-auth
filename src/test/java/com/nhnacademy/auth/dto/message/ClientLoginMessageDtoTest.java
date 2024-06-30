package com.nhnacademy.auth.dto.message;

import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ClientLoginMessageDtoTest {

    @Test
    void testNoArgsConstructor() {
        ClientLoginMessageDto dto = new ClientLoginMessageDto();
        assertThat(dto).isNotNull();
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        ClientLoginMessageDto dto = new ClientLoginMessageDto(1L, now);
        assertThat(dto.getClientId()).isEqualTo(1L);
        assertThat(dto.getLastLoginDate()).isEqualTo(now);
    }

    @Test
    void testSettersAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        ClientLoginMessageDto dto = new ClientLoginMessageDto();
        dto.setClientId(1L);
        dto.setLastLoginDate(now);

        assertThat(dto.getClientId()).isEqualTo(1L);
        assertThat(dto.getLastLoginDate()).isEqualTo(now);
    }

    @Test
    void testSerializable() {
        ClientLoginMessageDto dto = new ClientLoginMessageDto();
        assertThat(dto).isInstanceOf(Serializable.class);
    }
}
