package com.nhnacademy.auth.controller;

import com.nhnacademy.auth.dto.TokenResponseDto;
import com.nhnacademy.auth.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReissueController {
    private final JWTUtils jwtUtils;
    private final RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponseDto> reissue(@RequestHeader("refresh") String refresh) {
        Long id = null;
        if (refresh == null) {
            log.error("refresh is null");
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } else if (jwtUtils.isExpired(refresh)) {
            log.error("refresh expired token");
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        try {
            id = Long.valueOf(String.valueOf(redisTemplate.opsForHash().get(refresh, jwtUtils.getUUID(refresh))));
        } catch (NumberFormatException e) {
            log.error("refresh expired token");
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        String uuid = UUID.randomUUID().toString();
        String role = jwtUtils.getRole(refresh);
        String newRefresh = jwtUtils.createRefreshToken(uuid, role);

        redisTemplate.delete(refresh);
        redisTemplate.opsForHash().put(newRefresh, uuid, id);
        redisTemplate.expire(newRefresh, 14, TimeUnit.DAYS);
        return new ResponseEntity<>(new TokenResponseDto(jwtUtils.createAccessToken(uuid, role), newRefresh), HttpStatus.OK);
    }
}