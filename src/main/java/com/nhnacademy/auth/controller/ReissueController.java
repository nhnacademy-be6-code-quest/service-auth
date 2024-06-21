package com.nhnacademy.auth.controller;

import com.nhnacademy.auth.domain.RefreshEntity;
import com.nhnacademy.auth.domain.Role;
import com.nhnacademy.auth.dto.ClientLoginResponseDto;
import com.nhnacademy.auth.utils.JWTUtils;
import com.nhnacademy.auth.utils.RedisUtils;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReissueController {
    private final JWTUtils jwtUtils;
    private final RedisTemplate<String, String> redisTemplate;

    @PostMapping("/reissue")
    public ResponseEntity<ClientLoginResponseDto> reissue(@RequestHeader("refresh") String refresh) {
        if (refresh == null) {
            log.error("refresh is null");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        try {
            jwtUtils.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            log.error("refresh expired token");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        String rf = (String) redisTemplate.opsForHash().get(refresh, RedisUtils.getTokenPrefix());
        if (rf == null) {
            log.error("refresh expired token");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        String email = jwtUtils.getUserEmail(refresh);
        String name = jwtUtils.getUserName(refresh);
        String role = jwtUtils.getRole(refresh);
        String newRefresh = jwtUtils.createRefreshToken(email, name, role);
        HttpHeaders headers = new HttpHeaders();
        headers.set("access", jwtUtils.createAccessToken(email, name, role));
        headers.set("refresh", newRefresh);

        redisTemplate.delete(refresh);
        redisTemplate.opsForHash().put(newRefresh, RedisUtils.getTokenPrefix(), rf);
        redisTemplate.expire(newRefresh, 14, TimeUnit.DAYS);
        return new ResponseEntity<>(ClientLoginResponseDto.builder()
                .clientEmail(email)
                .clientName(name)
                .role(Role.valueOf(role))
                .build(), headers, HttpStatus.OK);
    }
}