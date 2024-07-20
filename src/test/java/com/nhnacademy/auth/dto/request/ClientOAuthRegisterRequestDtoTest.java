package com.nhnacademy.auth.dto.request;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ClientOAuthRegisterRequestDtoTest {

    @Test
    void testNoArgsConstructor() {
        ClientOAuthRegisterRequestDto dto = new ClientOAuthRegisterRequestDto();
        assertThat(dto.getIdentify()).isNull();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getBirth()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        ClientOAuthRegisterRequestDto dto = new ClientOAuthRegisterRequestDto("identify", "name", birthDate);
        assertThat(dto.getIdentify()).isEqualTo("identify");
        assertThat(dto.getName()).isEqualTo("name");
        assertThat(dto.getBirth()).isEqualTo(birthDate);
    }

    @Test
    void testSettersAndGetters() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        ClientOAuthRegisterRequestDto dto = new ClientOAuthRegisterRequestDto();
        dto.setIdentify("identify");
        dto.setName("name");
        dto.setBirth(birthDate);

        assertThat(dto.getIdentify()).isEqualTo("identify");
        assertThat(dto.getName()).isEqualTo("name");
        assertThat(dto.getBirth()).isEqualTo(birthDate);
    }

    @Test
    void testBuilder() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        ClientOAuthRegisterRequestDto dto = ClientOAuthRegisterRequestDto.builder()
                .identify("identify")
                .name("name")
                .birth(birthDate)
                .build();

        assertThat(dto.getIdentify()).isEqualTo("identify");
        assertThat(dto.getName()).isEqualTo("name");
        assertThat(dto.getBirth()).isEqualTo(birthDate);
    }
}
