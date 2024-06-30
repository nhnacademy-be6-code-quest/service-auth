package com.nhnacademy.auth.dto.request;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class OAuthRegisterRequestDtoTest {

    @Test
    void testNoArgsConstructor() {
        OAuthRegisterRequestDto dto = new OAuthRegisterRequestDto();
        assertThat(dto.getAccess()).isNull();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getBirth()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        OAuthRegisterRequestDto dto = new OAuthRegisterRequestDto("access", "name", birthDate);
        assertThat(dto.getAccess()).isEqualTo("access");
        assertThat(dto.getName()).isEqualTo("name");
        assertThat(dto.getBirth()).isEqualTo(birthDate);
    }

    @Test
    void testSettersAndGetters() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        OAuthRegisterRequestDto dto = new OAuthRegisterRequestDto();
        dto.setAccess("access");
        dto.setName("name");
        dto.setBirth(birthDate);

        assertThat(dto.getAccess()).isEqualTo("access");
        assertThat(dto.getName()).isEqualTo("name");
        assertThat(dto.getBirth()).isEqualTo(birthDate);
    }
}
