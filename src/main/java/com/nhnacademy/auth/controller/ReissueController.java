package com.nhnacademy.auth.controller;

import com.nhnacademy.auth.domain.RefreshEntity;
import com.nhnacademy.auth.domain.Role;
import com.nhnacademy.auth.dto.ClientLoginResponseDto;
import com.nhnacademy.auth.utils.JWTUtils;
import com.nhnacademy.auth.utils.RedisUtils;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class ReissueController {
    private final JWTUtils jwtUtils;
    private final RedisTemplate<String, String> redisTemplate;

    @PostMapping("/reissue")
    public ResponseEntity<ClientLoginResponseDto> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refresh = null;
        for (Cookie cookie :  request.getCookies()) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }
        if (refresh == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        try {
            jwtUtils.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        String category = jwtUtils.getCategory(refresh);
        if (!category.equals("refresh")) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        RefreshEntity rf = (RefreshEntity) redisTemplate.opsForHash().get(refresh, RedisUtils.getTokenPrefix());
        if (rf == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        String email = jwtUtils.getUserEmail(refresh);
        String name = jwtUtils.getUserName(refresh);
        String role = jwtUtils.getRole(refresh);
        String newRefresh = jwtUtils.createRefreshToken(email, name, role);
        response.setHeader("access", jwtUtils.createAccessToken(email, name, role));
        response.setHeader("refresh", newRefresh);

        redisTemplate.delete(refresh);
        redisTemplate.opsForHash().put(newRefresh, RedisUtils.getTokenPrefix(), rf);
        redisTemplate.expire(newRefresh, 14, TimeUnit.DAYS);
        return new ResponseEntity<>(ClientLoginResponseDto.builder()
                .clientEmail(email)
                .clientName(name)
                .role(Role.valueOf(role))
                .build(), HttpStatus.OK);
    }
}