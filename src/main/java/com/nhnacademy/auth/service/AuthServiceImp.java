package com.nhnacademy.auth.service;

import com.nhnacademy.auth.client.Client;
import com.nhnacademy.auth.dto.message.ClientLoginMessageDto;
import com.nhnacademy.auth.dto.response.ClientLoginResponseDto;
import com.nhnacademy.auth.dto.response.TokenResponseDto;
import com.nhnacademy.auth.exception.LoginFailException;
import com.nhnacademy.auth.exception.TokenInvalidationException;
import com.nhnacademy.auth.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {
    @Value("${rabbit.login.exchange.name}")
    private String loginExchangeName;
    @Value("${rabbit.login.routing.key}")
    private String loginRoutingKey;

    private final Client client;
    private final JWTUtils jwtUtils;
    private final RabbitTemplate rabbitTemplate;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public TokenResponseDto reissue(String refresh) {
        Long id = null;
        if (refresh == null) {
            throw new TokenInvalidationException("refresh is null");
        } else if (jwtUtils.isExpired(refresh)) {
            throw new TokenInvalidationException("refresh expired token");
        }
        try {
            id = Long.valueOf(String.valueOf(redisTemplate.opsForHash().get(refresh, jwtUtils.getUUID(refresh))));
        } catch (NumberFormatException e) {
            throw new TokenInvalidationException("refresh expired token");
        }

        String uuid = UUID.randomUUID().toString();
        String role = jwtUtils.getRole(refresh);
        String newRefresh = jwtUtils.createRefreshToken(uuid, role);
        redisTemplate.delete(refresh);
        addRefreshToken(id, uuid, newRefresh);
        return new TokenResponseDto(jwtUtils.createAccessToken(uuid, role), newRefresh);
    }

    @Override
    public TokenResponseDto login(String clientEmail, String clientPassword) {
        ClientLoginResponseDto response = client.login(clientEmail).getBody();
        if (response == null || !passwordEncoder.matches(clientPassword, response.getClientPassword())) {
            throw new LoginFailException("client login fail");
        }
        Long userId = response.getClientId();
        String role = response.getRole().getAuthority();
        String uuid = UUID.randomUUID().toString();
        String refresh = jwtUtils.createRefreshToken(uuid, role);
        addRefreshToken(userId, uuid, refresh);
        return new TokenResponseDto(jwtUtils.createAccessToken(uuid, role), refresh);
    }

    @Override
    public String logout(String refresh) {
        if (redisTemplate.opsForHash().get(refresh, jwtUtils.getUUID(refresh)) != null) {
            redisTemplate.delete(refresh);
        }
        return "Success";
    }

    private void addRefreshToken(Long userId, String uuid, String refresh) {
        redisTemplate.delete(refresh);
        redisTemplate.opsForHash().put(refresh, uuid, userId);
        redisTemplate.expire(refresh, 14, TimeUnit.DAYS);

        rabbitTemplate.convertAndSend(loginExchangeName, loginRoutingKey, new ClientLoginMessageDto(userId, LocalDateTime.now()));
    }
}
