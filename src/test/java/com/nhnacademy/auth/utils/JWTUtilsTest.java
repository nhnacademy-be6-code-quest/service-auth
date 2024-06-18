package com.nhnacademy.auth.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.jwt.secret=01234567890123456789012345678901" // 32 characters long secret key
})
public class JWTUtilsTest {

    @Value("${spring.jwt.secret}")
    private String secret;

    private JWTUtils jwtUtils;

    @BeforeEach
    public void setUp() {
        jwtUtils = new JWTUtils(secret);
    }

    @Test
    public void testCreateJwt() {
        String email = "test@example.com";
        String name = "Test User";
        String role = "USER";
        Long expiredMs = 1000L * 60 * 60; // 1 hour

        String token = jwtUtils.createJwt(email, name, role, expiredMs);

        assertThat(token).isNotNull();
    }

    @Test
    public void testGetUserEmail() {
        String email = "test@example.com";
        String name = "Test User";
        String role = "USER";
        Long expiredMs = 1000L * 60 * 60; // 1 hour

        String token = jwtUtils.createJwt(email, name, role, expiredMs);
        String extractedEmail = jwtUtils.getUserEmail(token);

        assertThat(extractedEmail).isEqualTo(email);
    }

    @Test
    public void testGetUserName() {
        String email = "test@example.com";
        String name = "Test User";
        String role = "USER";
        Long expiredMs = 1000L * 60 * 60; // 1 hour

        String token = jwtUtils.createJwt(email, name, role, expiredMs);
        String extractedName = jwtUtils.getUserName(token);

        assertThat(extractedName).isEqualTo(name);
    }

    @Test
    public void testGetRole() {
        String email = "test@example.com";
        String name = "Test User";
        String role = "USER";
        Long expiredMs = 1000L * 60 * 60; // 1 hour

        String token = jwtUtils.createJwt(email, name, role, expiredMs);
        String extractedRole = jwtUtils.getRole(token);

        assertThat(extractedRole).isEqualTo(role);
    }

    @Test
    public void testIsExpired() {
        String email = "test@example.com";
        String name = "Test User";
        String role = "USER";
        Long expiredMs = 1000L * 60 * 60; // 1 hour

        String token = jwtUtils.createJwt(email, name, role, expiredMs);

        assertThat(jwtUtils.isExpired(token)).isFalse();
    }
}
