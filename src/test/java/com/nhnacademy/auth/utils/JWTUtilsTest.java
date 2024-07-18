package com.nhnacademy.auth.utils;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JWTUtilsTest {

    private JWTUtils jwtUtils;
    private final String secretKeyString = "yourSecretKeyHereThatIsAtLeast32BytesLong";
    private final SecretKey secretKey = new SecretKeySpec(secretKeyString.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    private final Long accessExpiredMs = 3600000L; // 1 hour
    private final Long refreshExpiredMs = 86400000L; // 24 hours

    @BeforeEach
    void setUp() {
        jwtUtils = new JWTUtils(secretKey, accessExpiredMs, refreshExpiredMs);
    }

    @Test
    void testCreateAccessToken() {
        String uuid = "test-uuid";
        List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");
        String token = jwtUtils.createAccessToken(uuid, roles);

        assertNotNull(token);
        assertEquals("access", jwtUtils.getCategory(token));
        assertEquals(uuid, jwtUtils.getUUID(token));
        assertEquals(roles, jwtUtils.getRole(token));
        assertFalse(jwtUtils.isExpired(token));
    }

    @Test
    void testCreateRefreshToken() {
        String uuid = "test-uuid";
        List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");
        String token = jwtUtils.createRefreshToken(uuid, roles);

        assertNotNull(token);
        assertEquals("refresh", jwtUtils.getCategory(token));
        assertEquals(uuid, jwtUtils.getUUID(token));
        assertEquals(roles, jwtUtils.getRole(token));
        assertFalse(jwtUtils.isExpired(token));
    }

    @Test
    void testIsExpired() {
        String uuid = "test-uuid";
        List<String> roles = Arrays.asList("ROLE_USER");

        // Create a token that expires immediately
        String expiredToken = jwtUtils.createJwt("test", uuid, roles, -1000L);
        assertTrue(jwtUtils.isExpired(expiredToken));

        // Create a token that doesn't expire for a while
        String validToken = jwtUtils.createAccessToken(uuid, roles);
        assertFalse(jwtUtils.isExpired(validToken));
    }

    @Test
    void testGetClaimsFromToken() {
        String uuid = "test-uuid";
        List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");
        String token = jwtUtils.createAccessToken(uuid, roles);

        Claims claims = ReflectionTestUtils.invokeMethod(jwtUtils, "getClaimsFromToken", token);
        assertNotNull(claims);
        assertEquals("access", claims.get("category"));
        assertEquals(uuid, claims.get("uuid"));
        assertEquals(roles, claims.get("role"));
    }

    @Test
    void testInvalidToken() {
        String invalidToken = "invalidToken";
        assertThrows(Exception.class, () -> jwtUtils.getCategory(invalidToken));
        assertThrows(Exception.class, () -> jwtUtils.getUUID(invalidToken));
        assertThrows(Exception.class, () -> jwtUtils.getRole(invalidToken));
        assertTrue(jwtUtils.isExpired(invalidToken));
    }
}