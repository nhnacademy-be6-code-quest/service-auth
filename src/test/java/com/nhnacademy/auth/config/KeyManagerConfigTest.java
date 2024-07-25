package com.nhnacademy.auth.config;

import com.nhnacademy.auth.client.KeyManagerClient;
import io.jsonwebtoken.security.Keys;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class KeyManagerConfigTest {

    @Mock
    private KeyManagerClient keyManagerClient;

    @InjectMocks
    private KeyManagerConfig keyManagerConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(keyManagerConfig, "accessKeyId", "testAccessKeyId");
        ReflectionTestUtils.setField(keyManagerConfig, "accessKeySecret", "testAccessKeySecret");
    }

    private JSONObject createMockResponse(String key) {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("secret", key);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("body", bodyMap);

        return new JSONObject(responseMap);
    }

    @Test
    void testJwtSecretKey() {
        when(keyManagerClient.getJwtSecret(any(HttpHeaders.class))).thenReturn(createMockResponse("testJwtSecret"));

        SecretKey secretKey = keyManagerConfig.jwtSecretKey();

        assertNotNull(secretKey);
        assertEquals("HmacSHA256", secretKey.getAlgorithm());

        verify(keyManagerClient, times(1)).getJwtSecret(any(HttpHeaders.class));
    }

    @Test
    void testClientEncodingKey() {
        when(keyManagerClient.getClientEncoding(any(HttpHeaders.class))).thenReturn(createMockResponse("testClientEncoding"));

        String clientEncodingKey = keyManagerConfig.clientEncodingKey();

        assertEquals("testClientEncoding", clientEncodingKey);

        verify(keyManagerClient, times(1)).getClientEncoding(any(HttpHeaders.class));
    }

    @Test
    void testPaycoClientId() {
        when(keyManagerClient.getPaycoClientId(any(HttpHeaders.class))).thenReturn(createMockResponse("testPaycoClientId"));

        String paycoClientId = keyManagerConfig.paycoClientId();

        assertEquals("testPaycoClientId", paycoClientId);

        verify(keyManagerClient, times(1)).getPaycoClientId(any(HttpHeaders.class));
    }

    @Test
    void testPaycoClientSecret() {
        when(keyManagerClient.getPaycoClientSecret(any(HttpHeaders.class))).thenReturn(createMockResponse("testPaycoClientSecret"));

        String paycoClientSecret = keyManagerConfig.paycoClientSecret();

        assertEquals("testPaycoClientSecret", paycoClientSecret);

        verify(keyManagerClient, times(1)).getPaycoClientSecret(any(HttpHeaders.class));
    }

    @Test
    void testRedisKey() {
        when(keyManagerClient.getRedisHost(any(HttpHeaders.class))).thenReturn(createMockResponse("testRedisHost"));
        when(keyManagerClient.getRedisPassword(any(HttpHeaders.class))).thenReturn(createMockResponse("testRedisPassword"));
        when(keyManagerClient.getRedisPort(any(HttpHeaders.class))).thenReturn(createMockResponse("6379"));
        when(keyManagerClient.getRedisDb(any(HttpHeaders.class))).thenReturn(createMockResponse("0"));

        Map<String, String> redisKey = keyManagerConfig.redisKey();

        assertEquals("testRedisHost", redisKey.get("host"));
        assertEquals("testRedisPassword", redisKey.get("password"));
        assertEquals("6379", redisKey.get("port"));
        assertEquals("0", redisKey.get("db"));

        verify(keyManagerClient, times(1)).getRedisHost(any(HttpHeaders.class));
        verify(keyManagerClient, times(1)).getRedisPassword(any(HttpHeaders.class));
        verify(keyManagerClient, times(1)).getRedisPort(any(HttpHeaders.class));
        verify(keyManagerClient, times(1)).getRedisDb(any(HttpHeaders.class));
    }

    @Test
    void testRabbitKey() {
        when(keyManagerClient.getRabbitmqHost(any(HttpHeaders.class))).thenReturn(createMockResponse("testRabbitHost"));
        when(keyManagerClient.getRabbitmqPassword(any(HttpHeaders.class))).thenReturn(createMockResponse("testRabbitPassword"));
        when(keyManagerClient.getRabbitmqUsername(any(HttpHeaders.class))).thenReturn(createMockResponse("testRabbitUsername"));
        when(keyManagerClient.getRabbitmqPort(any(HttpHeaders.class))).thenReturn(createMockResponse("5672"));

        Map<String, String> rabbitKey = keyManagerConfig.rabbitKey();

        assertEquals("testRabbitHost", rabbitKey.get("host"));
        assertEquals("testRabbitPassword", rabbitKey.get("password"));
        assertEquals("testRabbitUsername", rabbitKey.get("username"));
        assertEquals("5672", rabbitKey.get("port"));

        verify(keyManagerClient, times(1)).getRabbitmqHost(any(HttpHeaders.class));
        verify(keyManagerClient, times(1)).getRabbitmqPassword(any(HttpHeaders.class));
        verify(keyManagerClient, times(1)).getRabbitmqUsername(any(HttpHeaders.class));
        verify(keyManagerClient, times(1)).getRabbitmqPort(any(HttpHeaders.class));
    }
}