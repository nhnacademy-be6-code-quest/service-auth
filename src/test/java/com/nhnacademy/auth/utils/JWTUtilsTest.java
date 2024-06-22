package com.nhnacademy.auth.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.jwt.secret=01234567890123456789012345678901",
        "spring.jwt.access.expiredMs=604800000", // in milliseconds
        "spring.jwt.refresh.expiredMs=31536000000" // in milliseconds
})
public class JWTUtilsTest {

    @Value("${spring.jwt.secret}")
    private String secret;

    @Value("${spring.jwt.access.expiredMs}")
    private Long accessExpiredMs;

    @Value("${spring.jwt.refresh.expiredMs}")
    private Long refreshExpiredMs;

    private JWTUtils jwtUtils;

    @BeforeEach
    public void setUp() {
        jwtUtils = new JWTUtils(secret, accessExpiredMs, refreshExpiredMs);
    }

    @Test
    public void testCreateJwt() {
        String uuid = "123e4567-e89b-12d3-a456-426614174000";
        String role = "ROLE_USER";
        Long expiredMs = 1000L * 60 * 60; // 1 hour

        String token = jwtUtils.createJwt("test", uuid, role, expiredMs);

        assertThat(token).isNotNull();
    }

    @Test
    public void testGetCategory() {
        String uuid = "123e4567-e89b-12d3-a456-426614174000";
        String role = "ROLE_USER";
        String category = "test";
        Long expiredMs = 1000L * 60 * 60; // 1 hour

        String token = jwtUtils.createJwt(category, uuid, role, expiredMs);
        String extractedCategory = jwtUtils.getCategory(token);

        assertThat(extractedCategory).isEqualTo(category);
    }

    @Test
    public void testGetUUID() {
        String uuid = "123e4567-e89b-12d3-a456-426614174000";
        String role = "ROLE_USER";
        String category = "test";
        Long expiredMs = 1000L * 60 * 60; // 1 hour

        String token = jwtUtils.createJwt(category, uuid, role, expiredMs);
        String extractedUUID = jwtUtils.getUUID(token);

        assertThat(extractedUUID).isEqualTo(uuid);
    }

    @Test
    public void testGetRole() {
        String uuid = "123e4567-e89b-12d3-a456-426614174000";
        String role = "ROLE_USER";
        String category = "test";
        Long expiredMs = 1000L * 60 * 60; // 1 hour

        String token = jwtUtils.createJwt(category, uuid, role, expiredMs);
        String extractedRole = jwtUtils.getRole(token);

        assertThat(extractedRole).isEqualTo(role);
    }

    @Test
    public void testIsExpired() {
        String uuid = "123e4567-e89b-12d3-a456-426614174000";
        String role = "ROLE_USER";
        String category = "test";
        Long expiredMs = 1000L * 60 * 60; // 1 hour

        String token = jwtUtils.createJwt(category, uuid, role, expiredMs);

        assertThat(jwtUtils.isExpired(token)).isFalse();
    }
}