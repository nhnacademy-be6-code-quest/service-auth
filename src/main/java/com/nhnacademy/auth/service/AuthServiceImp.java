package com.nhnacademy.auth.service;

import com.nhnacademy.auth.client.Client;
import com.nhnacademy.auth.dto.message.ClientLoginMessageDto;
import com.nhnacademy.auth.dto.request.ClientOAuthRegisterRequestDto;
import com.nhnacademy.auth.dto.response.ClientLoginResponseDto;
import com.nhnacademy.auth.dto.response.PaycoOAuthResponseDto;
import com.nhnacademy.auth.dto.response.PaycoUserInfoResponseDto;
import com.nhnacademy.auth.dto.response.TokenResponseDto;
import com.nhnacademy.auth.exception.DeletedClientException;
import com.nhnacademy.auth.exception.LoginFailException;
import com.nhnacademy.auth.exception.TokenInvalidationException;
import com.nhnacademy.auth.utils.JWTUtils;
import com.nhnacademy.auth.utils.TransformerUtils;
import feign.FeignException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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
    private final TransformerUtils transformerUtils;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public TokenResponseDto reissue(String refresh, String access) {
        validateTokens(refresh, access);

        String uuid = UUID.randomUUID().toString();
        String userId = String.valueOf(redisTemplate.opsForHash().get(refresh, jwtUtils.getUUID(refresh)));
        List<String> roles = jwtUtils.getRole(refresh);
        String newRefresh = jwtUtils.createRefreshToken(uuid, roles);
        String newAccess = jwtUtils.createAccessToken(transformerUtils.encode(userId), roles);

        addToken(getIdFromToken(refresh), uuid, newRefresh);
        redisTemplate.delete(refresh);
        return new TokenResponseDto(newAccess, newRefresh);
    }

    private void validateTokens(String refresh, String access) {
        if (refresh == null || access == null) {
            throw new TokenInvalidationException("token is null");
        } else if (jwtUtils.isExpired(refresh)) {
            throw new TokenInvalidationException("refresh expired token");
        }

        try {
            jwtUtils.getUUID(access);
        } catch (ExpiredJwtException e) {
            log.info("access expired token");
        } catch (JwtException e) {
            throw new TokenInvalidationException("invalid access token");
        }
    }

    private Long getIdFromToken(String refresh) {
        try {
            return Long.valueOf(String.valueOf(redisTemplate.opsForHash().get(refresh, jwtUtils.getUUID(refresh))));
        } catch (NumberFormatException e) {
            throw new TokenInvalidationException("invalid token");
        }
    }

    @Override
    public TokenResponseDto login(String clientEmail, String clientPassword) {
        ClientLoginResponseDto response = getClientLoginResponse(clientEmail);

        validateClientPassword(clientPassword, response);

        List<String> roles = response.getRole();
        String uuid = UUID.randomUUID().toString();
        String access = jwtUtils.createAccessToken(transformerUtils.encode(response.getClientId().toString()), roles);
        String refresh = jwtUtils.createRefreshToken(uuid, roles);
        addToken(response.getClientId(), uuid, refresh);
        return new TokenResponseDto(access, refresh);
    }

    private ClientLoginResponseDto getClientLoginResponse(String clientEmail) {
        try {
            return client.login(clientEmail).getBody();
        } catch (FeignException.Unauthorized | FeignException.NotFound e) {
            throw new LoginFailException("client login fail");
        } catch (FeignException.Gone e) {
            throw new DeletedClientException("deleted client fail");
        }
    }

    private void validateClientPassword(String clientPassword, ClientLoginResponseDto response) {
        if (response == null || !passwordEncoder.matches(clientPassword, response.getClientPassword())) {
            throw new LoginFailException("client login fail");
        }
    }

    @Override
    public String logout(String refresh, String access) {
        redisTemplate.delete(refresh);
        return "Success";
    }

    @Override
    public TokenResponseDto paycoOAuthLogin(String code) {
        String identifier = PAYCO_PREFIX + getPaycoUserInfo(code);

        try {
            ClientLoginResponseDto loginInfo = client.login(identifier).getBody();
            TokenResponseDto response = new TokenResponseDto(
                    jwtUtils.createAccessToken(transformerUtils.encode(loginInfo.getClientId().toString()), loginInfo.getRole()),
                    jwtUtils.createRefreshToken(identifier, loginInfo.getRole())
            );
            addToken(loginInfo.getClientId(), identifier, response.getRefresh());
            return response;
        } catch (FeignException.NotFound e) {
            return new TokenResponseDto(null, jwtUtils.createRefreshToken(identifier, List.of("ROLE_OAUTH")));
        } catch (FeignException.Gone e) {
            return null;
        }
    }

    @Override
    public String paycoOAuthRecovery(String code) {
        String identifier = PAYCO_PREFIX + getPaycoUserInfo(code);

        try {
            client.login(identifier);
        } catch (FeignException.Gone e) {
            return identifier;
        }
        return null;
    }

    @Override
    public TokenResponseDto oAuthRegister(String refresh, String name, LocalDate birth) {
        String uuid = jwtUtils.getUUID(refresh);
        client.createOauthClient(ClientOAuthRegisterRequestDto.builder()
                .identify(uuid)
                .name(name)
                .birth(birth)
                .build());

        ClientLoginResponseDto response = client.login(uuid).getBody();
        String accessToken = jwtUtils.createAccessToken(transformerUtils.encode(response.getClientId().toString()), response.getRole());
        String refreshToken = jwtUtils.createRefreshToken(uuid, response.getRole());
        addToken(response.getClientId(), uuid, refreshToken);
        return new TokenResponseDto(accessToken, refreshToken);
    }

    private String getPaycoTokenUri(String code) {
        return paycoTokenUri +
                "?grant_type=authorization_code" +
                "&code=" + code +
                "&client_id=" + paycoClientId +
                "&client_secret=" + paycoClientSecret;
    }

    private String getPaycoToken(String code) {
        ResponseEntity<PaycoOAuthResponseDto> response = restTemplate.getForEntity(getPaycoTokenUri(code), PaycoOAuthResponseDto.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new LoginFailException("payco login fail");
        }
        return response.getBody().getAccessToken();
    }

    private String getPaycoUserInfo(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("client_id", paycoClientId);
        headers.set("access_token", getPaycoToken(code));

        ResponseEntity<PaycoUserInfoResponseDto> response = restTemplate.postForEntity(paycoUserInfoUri, new HttpEntity<>(null, headers), PaycoUserInfoResponseDto.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new LoginFailException("payco login fail");
        }
        return response.getBody().getData().getMember().getIdNo();
    }

    private void addToken(Long userId, String uuid, String refresh) {
        redisTemplate.delete(refresh);
        redisTemplate.opsForHash().put(refresh, uuid, userId);
        redisTemplate.expire(refresh, 14, TimeUnit.DAYS);

        log.info("send login Message");
        rabbitTemplate.convertAndSend(loginExchangeName, loginRoutingKey, new ClientLoginMessageDto(userId, LocalDateTime.now()));
    }
}
