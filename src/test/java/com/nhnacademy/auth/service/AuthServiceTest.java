package com.nhnacademy.auth.service;

import com.nhnacademy.auth.client.Client;
import com.nhnacademy.auth.dto.response.ClientLoginResponseDto;
import com.nhnacademy.auth.dto.response.Role;
import com.nhnacademy.auth.dto.response.TokenResponseDto;
import com.nhnacademy.auth.exception.LoginFailException;
import com.nhnacademy.auth.exception.TokenInvalidationException;
import com.nhnacademy.auth.utils.JWTUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private Client client;

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @InjectMocks
    private AuthServiceImp authServiceImp;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    }

    @Test
    void testReissueValidToken() {
        String refresh = "valid_refresh_token";
        String uuid = UUID.randomUUID().toString();
        String role = "ROLE_USER";
        Long userId = 1L;

        when(jwtUtils.isExpired(refresh)).thenReturn(false);
        when(jwtUtils.getUUID(refresh)).thenReturn(uuid);
        when(hashOperations.get(refresh, uuid)).thenReturn(userId);
        when(jwtUtils.getRole(refresh)).thenReturn(role);
        when(jwtUtils.createRefreshToken(anyString(), eq(role))).thenReturn("new_refresh_token");
        when(jwtUtils.createAccessToken(anyString(), eq(role))).thenReturn("new_access_token");

        TokenResponseDto tokenResponseDto = authServiceImp.reissue(refresh);

        assertNotNull(tokenResponseDto);
        assertEquals("new_access_token", tokenResponseDto.getAccess());
        assertEquals("new_refresh_token", tokenResponseDto.getRefresh());

        verify(redisTemplate, times(1)).delete(refresh);
        verify(hashOperations, times(1)).put(anyString(), anyString(), eq(userId));
        verify(redisTemplate, times(1)).expire(anyString(), eq(14L), eq(TimeUnit.DAYS));
    }

    @Test
    void testReissueExpiredToken() {
        String refresh = "expired_refresh_token";

        when(jwtUtils.isExpired(refresh)).thenReturn(true);

        assertThrows(TokenInvalidationException.class, () -> authServiceImp.reissue(refresh));
    }

    @Test
    void testLoginSuccess() {
        String clientEmail = "test@example.com";
        String clientPassword = "password";
        String encodedPassword = "encoded_password";
        Long userId = 1L;
        String role = "ROLE_USER";
        String uuid = UUID.randomUUID().toString();
        ClientLoginResponseDto responseDto = new ClientLoginResponseDto(Role.valueOf(role), userId, clientEmail, encodedPassword, "hi");

        when(client.login(clientEmail)).thenReturn(ResponseEntity.ok(responseDto));
        when(passwordEncoder.matches(clientPassword, encodedPassword)).thenReturn(true);
        when(jwtUtils.createRefreshToken(anyString(), eq(role))).thenReturn("new_refresh_token");
        when(jwtUtils.createAccessToken(anyString(), eq(role))).thenReturn("new_access_token");

        TokenResponseDto tokenResponseDto = authServiceImp.login(clientEmail, clientPassword);

        assertNotNull(tokenResponseDto);
        assertEquals("new_access_token", tokenResponseDto.getAccess());
        assertEquals("new_refresh_token", tokenResponseDto.getRefresh());

        verify(redisTemplate, times(1)).delete(anyString());
        verify(hashOperations, times(1)).put(anyString(), anyString(), eq(userId));
        verify(redisTemplate, times(1)).expire(anyString(), eq(14L), eq(TimeUnit.DAYS));
    }

    @Test
    void testLoginFail() {
        String clientEmail = "test@example.com";
        String clientPassword = "wrong_password";
        String encodedPassword = "encoded_password";
        ClientLoginResponseDto responseDto = new ClientLoginResponseDto(Role.ROLE_USER, 1L, clientEmail, encodedPassword, "hi");

        when(client.login(clientEmail)).thenReturn(ResponseEntity.ok(responseDto));
        when(passwordEncoder.matches(clientPassword, encodedPassword)).thenReturn(false);

        assertThrows(LoginFailException.class, () -> authServiceImp.login(clientEmail, clientPassword));
    }

    @Test
    void testLogout() {
        String refresh = "valid_refresh_token";
        String uuid = UUID.randomUUID().toString();

        when(jwtUtils.getUUID(refresh)).thenReturn(uuid);
        when(hashOperations.get(refresh, uuid)).thenReturn(1L);

        String result = authServiceImp.logout(refresh);

        assertEquals("Success", result);
        verify(redisTemplate, times(1)).delete(refresh);
    }
}
