package com.nhnacademy.auth.dto.response;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ClientLoginResponseDtoTest {
    List<String> roles = List.of("ROLE_USER");

    @Test
    public void testAllArgsConstructor() {
        ClientLoginResponseDto dto = new ClientLoginResponseDto(roles, 1L, "test@example.com", "password", "Test User");

        assertThat(dto.getRole()).isEqualTo(roles);
        assertThat(dto.getClientEmail()).isEqualTo("test@example.com");
        assertThat(dto.getClientPassword()).isEqualTo("password");
        assertThat(dto.getClientName()).isEqualTo("Test User");
    }

    @Test
    public void testBuilder() {
        ClientLoginResponseDto dto = ClientLoginResponseDto.builder()
                .role(roles)
                .clientEmail("test@example.com")
                .clientPassword("password")
                .clientName("Test User")
                .build();

        assertThat(dto.getRole()).isEqualTo(roles);
        assertThat(dto.getClientEmail()).isEqualTo("test@example.com");
        assertThat(dto.getClientPassword()).isEqualTo("password");
        assertThat(dto.getClientName()).isEqualTo("Test User");
    }

    @Test
    public void testSettersAndGetters() {
        ClientLoginResponseDto dto = new ClientLoginResponseDto(roles, 1L, "test@example.com", "password", "Test User");

        dto.setRole(List.of("ROLE_ADMIN"));
        dto.setClientEmail("new@example.com");
        dto.setClientPassword("newpassword");
        dto.setClientName("New User");

        assertThat(dto.getRole()).isEqualTo(List.of("ROLE_ADMIN"));
        assertThat(dto.getClientEmail()).isEqualTo("new@example.com");
        assertThat(dto.getClientPassword()).isEqualTo("newpassword");
        assertThat(dto.getClientName()).isEqualTo("New User");
    }
}