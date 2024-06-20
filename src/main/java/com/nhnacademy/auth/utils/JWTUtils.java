package com.nhnacademy.auth.utils;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtils {
    private SecretKey secretKey;
    private Long accessExpiredMs;
    private Long refreshExpiredMs;

    public JWTUtils(
            @Value("${spring.jwt.secret}")String secret,
            @Value("${spring.jwt.access.expiredMs}")Long accessExpiredMs,
            @Value("${spring.jwt.refresh.expiredMs}")Long refreshExpiredMs) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.accessExpiredMs = accessExpiredMs;
        this.refreshExpiredMs = refreshExpiredMs;
    }

    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    public String getUserEmail(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }

    public String getUserName(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("name", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String createAccessToken(String userEmail, String userName, String role) {
        return createJwt("access", userEmail, userName, role, accessExpiredMs);
    }

    public String createRefreshToken(String userEmail, String userName, String role) {
        return createJwt("refresh", userEmail, userName, role, refreshExpiredMs);
    }

    public String createJwt(String category, String userEmail, String userName, String role, Long expiredMs) {
        return Jwts.builder()
                .claim("category", category)
                .claim("email", userEmail)
                .claim("name", userName)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }
}
