package com.nhnacademy.auth.service;

import com.nhnacademy.auth.client.Client;
import com.nhnacademy.auth.dto.message.ClientLoginMessageDto;
import com.nhnacademy.auth.dto.request.ClientOAuthRegisterRequestDto;
import com.nhnacademy.auth.dto.response.ClientLoginResponseDto;
import com.nhnacademy.auth.dto.response.PaycoOAuthResponseDto;
import com.nhnacademy.auth.dto.response.PaycoUserInfoResponseDto;
import com.nhnacademy.auth.dto.response.TokenResponseDto;
import com.nhnacademy.auth.exception.LoginFailException;
import com.nhnacademy.auth.exception.TokenInvalidationException;
import com.nhnacademy.auth.utils.JWTUtils;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {
    private static final String PAYCO_PREFIX = "payco_";

    @Value("${payco.client.id}")
    private String paycoClientId;
    @Value("${payco.client.secret}")
    private String paycoClientSecret;
    @Value("${payco.token.uri}")
    private String paycoTokenUri;
    @Value("${payco.user-info.uri}")
    private String paycoUserInfoUri;
    @Value("${rabbit.login.exchange.name}")
    private String loginExchangeName;
    @Value("${rabbit.login.routing.key}")
    private String loginRoutingKey;

    private final Client client;
    private final JWTUtils jwtUtils;
    private final RestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public TokenResponseDto reissue(String refresh, String access) {
        Long id = null;
        if (refresh == null || access == null) {
            throw new TokenInvalidationException("token is null");
        } else if (jwtUtils.isExpired(refresh)) {
            throw new TokenInvalidationException("refresh expired token");
        }
        try {
            id = Long.valueOf(String.valueOf(redisTemplate.opsForHash().get(refresh, jwtUtils.getUUID(access))));
        } catch (NumberFormatException e) {
            throw new TokenInvalidationException("invalid token");
        }

        String uuid = UUID.randomUUID().toString();
        List<String> roles = jwtUtils.getRole(refresh);
        String newRefresh = jwtUtils.createRefreshToken(uuid, roles);
        String newAccess = jwtUtils.createAccessToken(uuid, roles);

        redisTemplate.delete(access);
        redisTemplate.delete(refresh);
        addToken(id, uuid, newRefresh, newAccess);
        return new TokenResponseDto(newAccess, newRefresh);
    }

    @Override
    public TokenResponseDto login(String clientEmail, String clientPassword) {
        ClientLoginResponseDto response;
        try {
            response = client.login(clientEmail).getBody();
        } catch (FeignException e) {
            throw new LoginFailException("client login fail");
        }
        if (!passwordEncoder.matches(clientPassword, response.getClientPassword())) {
            throw new LoginFailException("client login fail");
        }
        Long userId = response.getClientId();
        List<String> roles = response.getRole();
        String uuid = UUID.randomUUID().toString();
        String access = jwtUtils.createAccessToken(uuid, roles);
        String refresh = jwtUtils.createRefreshToken(uuid, roles);
        addToken(userId, uuid, refresh, access);
        return new TokenResponseDto(access, refresh);
    }

    @Override
    public String logout(String refresh, String access) {
        redisTemplate.delete(access);
        redisTemplate.delete(refresh);
        return "Success";
    }

    @Override
    public TokenResponseDto paycoOAuthLogin(String code) {
        TokenResponseDto response;

        ResponseEntity<PaycoOAuthResponseDto> tokenResponse = restTemplate.getForEntity(getPaycoTokenUri(code), PaycoOAuthResponseDto.class);
        if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("client_id", paycoClientId);
        headers.set("access_token", tokenResponse.getBody().getAccessToken());
        ResponseEntity<PaycoUserInfoResponseDto> userInfoResponse = restTemplate.postForEntity(paycoUserInfoUri, new HttpEntity<>(null, headers), PaycoUserInfoResponseDto.class);
        if (!userInfoResponse.getStatusCode().is2xxSuccessful() || userInfoResponse.getBody() == null) {
            return null;
        }
        String identifier = PAYCO_PREFIX + userInfoResponse.getBody().getData().getMember().getIdNo();

        try {
            ClientLoginResponseDto loginInfo = client.login(identifier).getBody();
            response = new TokenResponseDto(
                    jwtUtils.createAccessToken(identifier, loginInfo.getRole()),
                    jwtUtils.createRefreshToken(identifier, loginInfo.getRole())
            );
            addToken(loginInfo.getClientId(), identifier, response.getRefresh(), response.getAccess());
        } catch (FeignException.Unauthorized e) {
            response = new TokenResponseDto(
                    null,
                    jwtUtils.createAccessToken(identifier, List.of("ROLE_OAUTH"))
            );
        } catch (FeignException.Gone e) {
            response = null;
        }
        return response;
    }

    @Override
    public String paycoOAuthRecovery(String code) {
        ResponseEntity<PaycoOAuthResponseDto> tokenResponse = restTemplate.getForEntity(getPaycoTokenUri(code), PaycoOAuthResponseDto.class);
        if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("client_id", paycoClientId);
        headers.set("access_token", tokenResponse.getBody().getAccessToken());
        ResponseEntity<PaycoUserInfoResponseDto> userInfoResponse = restTemplate.postForEntity(paycoUserInfoUri, new HttpEntity<>(null, headers), PaycoUserInfoResponseDto.class);
        if (!userInfoResponse.getStatusCode().is2xxSuccessful() || userInfoResponse.getBody() == null) {
            return null;
        }
        String identifier = PAYCO_PREFIX + userInfoResponse.getBody().getData().getMember().getIdNo();

        try {
            client.login(identifier);
        } catch (FeignException.Gone e) {
            return identifier;
        }
        return null;
    }

    @Override
    public TokenResponseDto oAuthRegister(String access, String name, LocalDate birth) {
        String uuid = jwtUtils.getUUID(access);
        client.createOauthClient(ClientOAuthRegisterRequestDto.builder()
                .identify(uuid)
                .name(name)
                .birth(birth)
                .build());

        ClientLoginResponseDto response = client.login(uuid).getBody();
        String accessToken = jwtUtils.createAccessToken(uuid, response.getRole());
        String refreshToken = jwtUtils.createRefreshToken(uuid, response.getRole());
        addToken(response.getClientId(), uuid, refreshToken, accessToken);
        return new TokenResponseDto(accessToken, refreshToken);
    }

    private String getPaycoTokenUri(String code) {
        return paycoTokenUri +
                "?grant_type=authorization_code" +
                "&code=" + code +
                "&client_id=" + paycoClientId +
                "&client_secret=" + paycoClientSecret;
    }

    private void addToken(Long userId, String uuid, String refresh, String access) {
        redisTemplate.delete(refresh);
        redisTemplate.opsForHash().put(refresh, uuid, userId);
        redisTemplate.expire(refresh, 14, TimeUnit.DAYS);

        redisTemplate.delete(access);
        redisTemplate.opsForHash().put(access, uuid, userId);
        redisTemplate.expire(access, 2, TimeUnit.HOURS);

        log.info("send login Message");
        rabbitTemplate.convertAndSend(loginExchangeName, loginRoutingKey, new ClientLoginMessageDto(userId, LocalDateTime.now()));
    }
}
