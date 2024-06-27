package com.nhnacademy.auth.client;

import com.nhnacademy.auth.dto.response.ClientLoginResponseDto;
import com.nhnacademy.auth.dto.response.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class ClientTest {

    @Autowired
    private Client client;

    @MockBean
    private Client mockClient;

    @Test
    public void testLogin() {
        // Mocking the Client interface
        ClientLoginResponseDto responseDto = new ClientLoginResponseDto(Role.ROLE_USER, 1L, "test@example.com", "password", "Test User");
        ResponseEntity<ClientLoginResponseDto> responseEntity = ResponseEntity.ok(responseDto);

        when(mockClient.login(anyString())).thenReturn(responseEntity);

        // Actual test
        ResponseEntity<ClientLoginResponseDto> result = mockClient.login("test@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getClientEmail()).isEqualTo("test@example.com");
        assertThat(result.getBody().getClientName()).isEqualTo("Test User");
        assertThat(result.getBody().getRole()).isEqualTo(Role.ROLE_USER);
    }
}
