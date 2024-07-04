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
import org.springframework.http.HttpStatus;
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
        redisTemplate.delete(refresh);
        addRefreshToken(id, uuid, newRefresh);
        return new TokenResponseDto(jwtUtils.createAccessToken(uuid, roles), newRefresh);
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
        String refresh = jwtUtils.createRefreshToken(uuid, roles);
        addRefreshToken(userId, uuid, refresh);
        return new TokenResponseDto(jwtUtils.createAccessToken(uuid, roles), refresh);
    }

    @Override
    public String logout(String refresh) {
        if (redisTemplate.opsForHash().get(refresh, jwtUtils.getUUID(refresh)) != null) {
            redisTemplate.delete(refresh);
        }
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
            addRefreshToken(loginInfo.getClientId(), identifier, response.getRefresh());
        } catch (FeignException.Unauthorized e) {
            response = new TokenResponseDto(
                    jwtUtils.createAccessToken(identifier, List.of("ROLE_OAUTH")),
                    null
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
        addRefreshToken(response.getClientId(), uuid, refreshToken);
        return new TokenResponseDto(accessToken, refreshToken);
    }

    private String getPaycoTokenUri(String code) {
        return paycoTokenUri +
                "?grant_type=authorization_code" +
                "&code=" + code +
                "&client_id=" + paycoClientId +
                "&client_secret=" + paycoClientSecret;
    }

    private void addRefreshToken(Long userId, String uuid, String refresh) {
        redisTemplate.delete(refresh);
        redisTemplate.opsForHash().put(refresh, uuid, userId);
        redisTemplate.expire(refresh, 14, TimeUnit.DAYS);

        log.info("send login Message");
        rabbitTemplate.convertAndSend(loginExchangeName, loginRoutingKey, new ClientLoginMessageDto(userId, LocalDateTime.now()));
    }
}
