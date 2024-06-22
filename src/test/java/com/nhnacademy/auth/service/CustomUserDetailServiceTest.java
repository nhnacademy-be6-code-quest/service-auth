package com.nhnacademy.auth.service;

import com.nhnacademy.auth.client.Client;
import com.nhnacademy.auth.domain.Role;
import com.nhnacademy.auth.dto.ClientLoginResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CustomUserDetailServiceTest {

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @MockBean
    private Client client;

    private ClientLoginResponseDto clientLoginResponseDto;

    @BeforeEach
    public void setUp() {
        clientLoginResponseDto = ClientLoginResponseDto.builder()
                .clientEmail("test@example.com")
                .clientPassword("password")
                .clientName("Test User")
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    public void testLoadUserByUsernameSuccess() {
        when(client.login(anyString())).thenReturn(ResponseEntity.ok(clientLoginResponseDto));

        UserDetails userDetails = customUserDetailService.loadUserByUsername("test@example.com");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("test@example.com");
        assertThat(userDetails.getPassword()).isEqualTo("password");
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
    }

    @Test
    public void testLoadUserByUsernameNotFound() {
        when(client.login(anyString())).thenReturn(ResponseEntity.ofNullable(null));

        assertThat(customUserDetailService.loadUserByUsername("notfound@example.com")).isNull();
    }
}
