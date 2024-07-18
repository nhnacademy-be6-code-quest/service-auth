package com.nhnacademy.auth.config;

import com.nhnacademy.auth.client.KeyManagerClient;
import io.jsonwebtoken.SignatureAlgorithm;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestPropertySource(properties = {"user.access.key.id=test-access-id", "secret.access.key=test-access-secret"})
class KeyManagerConfigTest {

    @Mock
    private KeyManagerClient keyManagerClient;

    @InjectMocks
    private KeyManagerConfig keyManagerConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        keyManagerConfig = new KeyManagerConfig(keyManagerClient);
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
        String expectedSecret = "test-jwt-secret";
        when(keyManagerClient.getJwtSecret(any(HttpHeaders.class))).thenReturn(createMockResponse(expectedSecret));

        SecretKey secretKey = keyManagerConfig.jwtSecretKey();
        assertNotNull(secretKey);
        assertEquals(new SecretKeySpec(expectedSecret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName()).getAlgorithm(), secretKey.getAlgorithm());

        verify(keyManagerClient, times(1)).getJwtSecret(any(HttpHeaders.class));
    }

    @Test
    void testClientEncodingKey() {
        String expectedKey = "test-client-encoding";
        when(keyManagerClient.getClientEncoding(any(HttpHeaders.class))).thenReturn(createMockResponse(expectedKey));

        String clientEncodingKey = keyManagerConfig.clientEncodingKey();
        assertEquals(expectedKey, clientEncodingKey);

        verify(keyManagerClient, times(1)).getClientEncoding(any(HttpHeaders.class));
    }

    @Test
    void testRedisHost() {
        String expectedKey = "test-redis-host";
        when(keyManagerClient.getRedisHost(any(HttpHeaders.class))).thenReturn(createMockResponse(expectedKey));

        String redisHost = keyManagerConfig.redisHost();
        assertEquals(expectedKey, redisHost);

        verify(keyManagerClient, times(1)).getRedisHost(any(HttpHeaders.class));
    }

    @Test
    void testRedisPassword() {
        String expectedKey = "test-redis-password";
        when(keyManagerClient.getRedisPassword(any(HttpHeaders.class))).thenReturn(createMockResponse(expectedKey));

        String redisPassword = keyManagerConfig.redisPassword();
        assertEquals(expectedKey, redisPassword);

        verify(keyManagerClient, times(1)).getRedisPassword(any(HttpHeaders.class));
    }

    @Test
    void testRedisPort() {
        String expectedKey = "6379";
        when(keyManagerClient.getRedisPort(any(HttpHeaders.class))).thenReturn(createMockResponse(expectedKey));

        Integer redisPort = keyManagerConfig.redisPort();
        assertEquals(Integer.valueOf(expectedKey), redisPort);

        verify(keyManagerClient, times(1)).getRedisPort(any(HttpHeaders.class));
    }

    @Test
    void testRedisDb() {
        String expectedKey = "0";
        when(keyManagerClient.getRedisDb(any(HttpHeaders.class))).thenReturn(createMockResponse(expectedKey));

        Integer redisDb = keyManagerConfig.redisDb();
        assertEquals(Integer.valueOf(expectedKey), redisDb);

        verify(keyManagerClient, times(1)).getRedisDb(any(HttpHeaders.class));
    }

    @Test
    void testRabbitHost() {
        String expectedKey = "test-rabbit-host";
        when(keyManagerClient.getRabbitmqHost(any(HttpHeaders.class))).thenReturn(createMockResponse(expectedKey));

        String rabbitHost = keyManagerConfig.rabbitHost();
        assertEquals(expectedKey, rabbitHost);

        verify(keyManagerClient, times(1)).getRabbitmqHost(any(HttpHeaders.class));
    }

    @Test
    void testRabbitPassword() {
        String expectedKey = "test-rabbit-password";
        when(keyManagerClient.getRabbitmqPassword(any(HttpHeaders.class))).thenReturn(createMockResponse(expectedKey));

        String rabbitPassword = keyManagerConfig.rabbitPassword();
        assertEquals(expectedKey, rabbitPassword);

        verify(keyManagerClient, times(1)).getRabbitmqPassword(any(HttpHeaders.class));
    }

    @Test
    void testRabbitUsername() {
        String expectedKey = "test-rabbit-username";
        when(keyManagerClient.getRabbitmqUsername(any(HttpHeaders.class))).thenReturn(createMockResponse(expectedKey));

        String rabbitUsername = keyManagerConfig.rabbitUsername();
        assertEquals(expectedKey, rabbitUsername);

        verify(keyManagerClient, times(1)).getRabbitmqUsername(any(HttpHeaders.class));
    }

    @Test
    void testRabbitPort() {
        String expectedKey = "5672";
        when(keyManagerClient.getRabbitmqPort(any(HttpHeaders.class))).thenReturn(createMockResponse(expectedKey));

        Integer rabbitPort = keyManagerConfig.rabbitPort();
        assertEquals(Integer.valueOf(expectedKey), rabbitPort);

        verify(keyManagerClient, times(1)).getRabbitmqPort(any(HttpHeaders.class));
    }

    @Test
    void testPaycoClientId() {
        String expectedKey = "test-payco-client-id";
        when(keyManagerClient.getPaycoClientId(any(HttpHeaders.class))).thenReturn(createMockResponse(expectedKey));

        String paycoClientId = keyManagerConfig.paycoClientId();
        assertEquals(expectedKey, paycoClientId);

        verify(keyManagerClient, times(1)).getPaycoClientId(any(HttpHeaders.class));
    }

    @Test
    void testPaycoClientSecret() {
        String expectedKey = "test-payco-client-secret";
        when(keyManagerClient.getPaycoClientSecret(any(HttpHeaders.class))).thenReturn(createMockResponse(expectedKey));

        String paycoClientSecret = keyManagerConfig.paycoClientSecret();
        assertEquals(expectedKey, paycoClientSecret);

        verify(keyManagerClient, times(1)).getPaycoClientSecret(any(HttpHeaders.class));
    }
}
