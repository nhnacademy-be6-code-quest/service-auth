package com.nhnacademy.auth.service;

import com.nhnacademy.auth.client.Client;
import com.nhnacademy.auth.dto.request.ClientOAuthRegisterRequestDto;
import com.nhnacademy.auth.dto.response.*;
import com.nhnacademy.auth.exception.LoginFailException;
import com.nhnacademy.auth.exception.TokenInvalidationException;
import com.nhnacademy.auth.utils.JWTUtils;
import com.nhnacademy.auth.utils.TransformerUtils;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private Client client;

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private TransformerUtils transformerUtils;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuthServiceImp authServiceImp;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        ReflectionTestUtils.setField(authServiceImp, "paycoClientId", "payco_client_id");
        ReflectionTestUtils.setField(authServiceImp, "paycoClientSecret", "payco_client_secret");
        ReflectionTestUtils.setField(authServiceImp, "paycoTokenUri", "http://payco/token");
        ReflectionTestUtils.setField(authServiceImp, "paycoUserInfoUri", "http://payco/userinfo");
    }

    @Test
    void testReissueValidToken() {
        String access = "valid_access_token";
        String refresh = "valid_refresh_token";
        String uuid = UUID.randomUUID().toString();
        String role = "ROLE_USER";
        Long userId = 1L;

        when(jwtUtils.isExpired(refresh)).thenReturn(false);
        when(jwtUtils.getUUID(refresh)).thenReturn(uuid);
        when(hashOperations.get(refresh, uuid)).thenReturn(userId);
        when(jwtUtils.getRole(refresh)).thenReturn(List.of(role));
        when(jwtUtils.createRefreshToken(anyString(), eq(List.of(role)))).thenReturn("new_refresh_token");
        when(jwtUtils.createAccessToken(anyString(), eq(List.of(role)))).thenReturn("new_access_token");
        when(jwtUtils.getUUID(access)).thenReturn("new_access_token");
        when(transformerUtils.encode(anyString())).thenReturn("new_access_token");

        TokenResponseDto tokenResponseDto = authServiceImp.reissue(refresh, access);

        assertNotNull(tokenResponseDto);
        assertEquals("new_access_token", tokenResponseDto.getAccess());
        assertEquals("new_refresh_token", tokenResponseDto.getRefresh());

        verify(redisTemplate, times(2)).delete(anyString());
        verify(hashOperations, times(1)).put(anyString(), anyString(), eq(userId));
        verify(redisTemplate, times(1)).expire(anyString(), eq(14L), eq(TimeUnit.DAYS));
    }

    @Test
    void testReissueExpiredToken() {
        String access = "valid_access_token";
        String refresh = "expired_refresh_token";

        when(jwtUtils.isExpired(refresh)).thenReturn(true);

        assertThrows(TokenInvalidationException.class, () -> authServiceImp.reissue(refresh, access));
    }

    @Test
    void testLoginSuccess() {
        String clientEmail = "test@example.com";
        String clientPassword = "password";
        String encodedPassword = "encoded_password";
        Long userId = 1L;
        String role = "ROLE_USER";
        String uuid = UUID.randomUUID().toString();
        ClientLoginResponseDto responseDto = new ClientLoginResponseDto(List.of(role), userId, clientEmail, encodedPassword, "hi");

        when(client.login(clientEmail)).thenReturn(ResponseEntity.ok(responseDto));
        when(passwordEncoder.matches(clientPassword, encodedPassword)).thenReturn(true);
        when(jwtUtils.createRefreshToken(anyString(), eq(List.of(role)))).thenReturn("new_refresh_token");
        when(jwtUtils.createAccessToken(anyString(), eq(List.of(role)))).thenReturn("new_access_token");
        when(transformerUtils.encode(userId.toString())).thenReturn("new_access_token");

        TokenResponseDto tokenResponseDto = authServiceImp.login(clientEmail, clientPassword);

        assertNotNull(tokenResponseDto);
        assertEquals("new_access_token", tokenResponseDto.getAccess());
        assertEquals("new_refresh_token", tokenResponseDto.getRefresh());

        verify(redisTemplate, times(1)).delete(anyString());
        verify(hashOperations, times(1)).put(anyString(), anyString(), eq(userId));
        verify(redisTemplate, times(1)).expire(anyString(), eq(14L), eq(TimeUnit.DAYS));
    }

    @Test
    void testLoginFail() {
        String clientEmail = "test@example.com";
        String clientPassword = "wrong_password";
        String encodedPassword = "encoded_password";
        ClientLoginResponseDto responseDto = new ClientLoginResponseDto(List.of("ROLE_USER"), 1L, clientEmail, encodedPassword, "hi");

        when(client.login(clientEmail)).thenReturn(ResponseEntity.ok(responseDto));
        when(passwordEncoder.matches(clientPassword, encodedPassword)).thenReturn(false);

        assertThrows(LoginFailException.class, () -> authServiceImp.login(clientEmail, clientPassword));
    }

    @Test
    void testLogout() {
        String access = "valid_access_token";
        String refresh = "valid_refresh_token";
        String uuid = UUID.randomUUID().toString();

        when(jwtUtils.getUUID(refresh)).thenReturn(uuid);
        when(hashOperations.get(refresh, uuid)).thenReturn(1L);

        String result = authServiceImp.logout(refresh, access);

        assertEquals("Success", result);
        verify(redisTemplate, times(1)).delete(refresh);
    }

    @Test
    void testPaycoOAuthLoginSuccess() {
        String code = "valid_code";
        PaycoOAuthResponseDto tokenResponseDto = new PaycoOAuthResponseDto();
        tokenResponseDto.setAccessToken("access_token");
        tokenResponseDto.setRefreshToken("refresh_token");

        PaycoUserInfoResponseDto userInfoResponseDto = new PaycoUserInfoResponseDto();
        PaycoUserInfoResponseDto.Member member = new PaycoUserInfoResponseDto.Member();
        member.setIdNo("12345");
        member.setEmail("email@example.com");
        userInfoResponseDto.setData(new PaycoUserInfoResponseDto.Data());
        userInfoResponseDto.getData().setMember(member);

        RestTemplate restTemplate = mock(RestTemplate.class);
        HttpHeaders headers = new HttpHeaders();
        headers.set("client_id", "payco_client_id");
        headers.set("access_token", "access_token");

        when(restTemplate.getForEntity(anyString(), eq(PaycoOAuthResponseDto.class))).thenReturn(ResponseEntity.ok(tokenResponseDto));
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(PaycoUserInfoResponseDto.class))).thenReturn(ResponseEntity.ok(userInfoResponseDto));

        authServiceImp = new AuthServiceImp(client, jwtUtils, restTemplate, rabbitTemplate, passwordEncoder, transformerUtils, redisTemplate);
        ReflectionTestUtils.setField(authServiceImp, "paycoClientId", "payco_client_id");
        ReflectionTestUtils.setField(authServiceImp, "paycoTokenUri", "http://payco/token");
        ReflectionTestUtils.setField(authServiceImp, "paycoUserInfoUri", "http://payco/userinfo");

        ClientLoginResponseDto loginInfo = new ClientLoginResponseDto(List.of("ROLE_OAUTH"), 1L, "payco_12345", "encoded_password", "hi");
        when(client.login(anyString())).thenReturn(ResponseEntity.ok(loginInfo));

        TokenResponseDto response = authServiceImp.paycoOAuthLogin(code);

        assertNotNull(response);
        verify(client, times(1)).login(anyString());
    }

    @Test
    void testPaycoOAuthRecoverySuccess() {
        String code = "valid_code";
        PaycoOAuthResponseDto tokenResponseDto = new PaycoOAuthResponseDto();
        tokenResponseDto.setAccessToken("access_token");
        tokenResponseDto.setRefreshToken("refresh_token");

        PaycoUserInfoResponseDto userInfoResponseDto = new PaycoUserInfoResponseDto();
        PaycoUserInfoResponseDto.Member member = new PaycoUserInfoResponseDto.Member();
        member.setIdNo("12345");
        member.setEmail("email@example.com");
        userInfoResponseDto.setData(new PaycoUserInfoResponseDto.Data());
        userInfoResponseDto.getData().setMember(member);

        RestTemplate restTemplate = mock(RestTemplate.class);
        HttpHeaders headers = new HttpHeaders();
        headers.set("client_id", "payco_client_id");
        headers.set("access_token", "access_token");

        when(restTemplate.getForEntity(anyString(), eq(PaycoOAuthResponseDto.class))).thenReturn(ResponseEntity.ok(tokenResponseDto));
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(PaycoUserInfoResponseDto.class))).thenReturn(ResponseEntity.ok(userInfoResponseDto));

        authServiceImp = new AuthServiceImp(client, jwtUtils, restTemplate, rabbitTemplate, passwordEncoder, transformerUtils, redisTemplate);
        ReflectionTestUtils.setField(authServiceImp, "paycoClientId", "payco_client_id");
        ReflectionTestUtils.setField(authServiceImp, "paycoTokenUri", "http://payco/token");
        ReflectionTestUtils.setField(authServiceImp, "paycoUserInfoUri", "http://payco/userinfo");

        Request request = Request.create(Request.HttpMethod.GET, "url", Map.of(), null, new RequestTemplate());
        when(client.login(anyString())).thenThrow(new FeignException.Gone("", request, null, null));

        String response = authServiceImp.paycoOAuthRecovery(code);

        assertEquals("payco_12345", response);
        verify(client, times(1)).login(anyString());
    }

    @Test
    void testOAuthRegister() {
        String access = "access_token";
        String name = "John Doe";
        LocalDate birth = LocalDate.of(1990, 1, 1);
        String uuid = UUID.randomUUID().toString();
        ClientLoginResponseDto responseDto = new ClientLoginResponseDto(List.of("ROLE_USER"), 1L, "email@example.com", "encoded_password", "hi");

        when(jwtUtils.getUUID(access)).thenReturn(uuid);
        when(client.createOauthClient(any(ClientOAuthRegisterRequestDto.class))).thenReturn(ResponseEntity.ok("Success"));
        when(client.login(uuid)).thenReturn(ResponseEntity.ok(responseDto));
        when(jwtUtils.createAccessToken(anyString(), eq(List.of("ROLE_USER")))).thenReturn("new_access_token");
        when(jwtUtils.createRefreshToken(anyString(), eq(List.of("ROLE_USER")))).thenReturn("new_refresh_token");
        when(transformerUtils.encode(responseDto.getClientId().toString())).thenReturn("new_client_id");

        TokenResponseDto tokenResponseDto = authServiceImp.oAuthRegister(access, name, birth);

        assertNotNull(tokenResponseDto);
        assertEquals("new_access_token", tokenResponseDto.getAccess());
        assertEquals("new_refresh_token", tokenResponseDto.getRefresh());

        verify(client, times(1)).createOauthClient(any(ClientOAuthRegisterRequestDto.class));
        verify(redisTemplate, times(1)).delete(anyString());
        verify(hashOperations, times(1)).put(anyString(), anyString(), eq(1L));
        verify(redisTemplate, times(1)).expire(anyString(), eq(14L), eq(TimeUnit.DAYS));
    }
}