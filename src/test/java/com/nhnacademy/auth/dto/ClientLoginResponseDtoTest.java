package com.nhnacademy.auth.dto;

import com.nhnacademy.auth.domain.Role;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ClientLoginResponseDtoTest {

    @Test
    public void testAllArgsConstructor() {
        ClientLoginResponseDto dto = new ClientLoginResponseDto(Role.ROLE_USER, 1L, "test@example.com", "password", "Test User");

        assertThat(dto.getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(dto.getClientEmail()).isEqualTo("test@example.com");
        assertThat(dto.getClientPassword()).isEqualTo("password");
        assertThat(dto.getClientName()).isEqualTo("Test User");
    }

    @Test
    public void testBuilder() {
        ClientLoginResponseDto dto = ClientLoginResponseDto.builder()
                .role(Role.ROLE_USER)
                .clientEmail("test@example.com")
                .clientPassword("password")
                .clientName("Test User")
                .build();

        assertThat(dto.getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(dto.getClientEmail()).isEqualTo("test@example.com");
        assertThat(dto.getClientPassword()).isEqualTo("password");
        assertThat(dto.getClientName()).isEqualTo("Test User");
    }

    @Test
    public void testSettersAndGetters() {
        ClientLoginResponseDto dto = new ClientLoginResponseDto(Role.ROLE_USER, 1L, "test@example.com", "password", "Test User");

        dto.setRole(Role.ROLE_ADMIN);
        dto.setClientEmail("new@example.com");
        dto.setClientPassword("newpassword");
        dto.setClientName("New User");

        assertThat(dto.getRole()).isEqualTo(Role.ROLE_ADMIN);
        assertThat(dto.getClientEmail()).isEqualTo("new@example.com");
        assertThat(dto.getClientPassword()).isEqualTo("newpassword");
        assertThat(dto.getClientName()).isEqualTo("New User");
    }
}