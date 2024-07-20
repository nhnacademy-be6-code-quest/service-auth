package com.nhnacademy.auth.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTUtils {
    private final SecretKey jwtSecretKey;
    @Value("${spring.jwt.access.expiredMs}")
    private Long accessExpiredMs;
    @Value("${spring.jwt.refresh.expiredMs}")
    private Long refreshExpiredMs;

    public String getCategory(String token) {
        return getClaimsFromToken(token).get("category", String.class);
    }

    public String getUUID(String token) {
        return getClaimsFromToken(token).get("uuid", String.class);
    }

    public List<String> getRole(String token) {
        return getClaimsFromToken(token).get("role", List.class);
    }

    public boolean isExpired(String token) {
        try {
            return getClaimsFromToken(token).getExpiration().before(new Date());
        } catch (Exception e) {
            log.error(e.getMessage());
            return true;
        }
    }

    private Claims getClaimsFromToken(String token) throws JwtException {
        return Jwts.parser().verifyWith(jwtSecretKey).build().parseSignedClaims(token).getPayload();
    }

    public String createAccessToken(String uuid, List<String> roles) {
        return createJwt("access", uuid, roles, accessExpiredMs);
    }

    public String createRefreshToken(String uuid, List<String> roles) {
        return createJwt("refresh", uuid, roles, refreshExpiredMs);
    }

    public String createJwt(String category, String uuid, List<String> roles, Long expiredMs) {
        return Jwts.builder()
                .claim("category", category)
                .claim("uuid", uuid)
                .claim("role", roles)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(jwtSecretKey)
                .compact();
    }
}
