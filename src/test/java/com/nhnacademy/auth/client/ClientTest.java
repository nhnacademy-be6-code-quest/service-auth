package com.nhnacademy.auth.client;

import com.nhnacademy.auth.dto.request.ClientOAuthRegisterRequestDto;
import com.nhnacademy.auth.dto.response.ClientLoginResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class ClientTest {

    @Autowired
    private Client client;

    @MockBean
    private Client mockClient;

    @Test
    void testLogin() {
        List<String> roles = List.of("ROLE_USER");
        // Mocking the Client interface
        ClientLoginResponseDto responseDto = new ClientLoginResponseDto(roles, 1L, "test@example.com", "password", "Test User");
        ResponseEntity<ClientLoginResponseDto> responseEntity = ResponseEntity.ok(responseDto);

        when(mockClient.login(anyString())).thenReturn(responseEntity);

        // Actual test
        ResponseEntity<ClientLoginResponseDto> result = mockClient.login("test@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getClientEmail()).isEqualTo("test@example.com");
        assertThat(result.getBody().getClientName()).isEqualTo("Test User");
        assertThat(result.getBody().getRole()).isEqualTo(roles);
    }

    @Test
    void testCreateOauthClient() {
        // Mocking the Client interface
        ClientOAuthRegisterRequestDto requestDto = new ClientOAuthRegisterRequestDto("client_id", "client_secret", LocalDate.now());
        ResponseEntity<String> responseEntity = new ResponseEntity<>("Success", HttpStatus.OK);

        when(mockClient.createOauthClient(any(ClientOAuthRegisterRequestDto.class))).thenReturn(responseEntity);

        // Actual test
        ResponseEntity<String> result = mockClient.createOauthClient(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(result.getBody()).isEqualTo("Success");
    }
}
