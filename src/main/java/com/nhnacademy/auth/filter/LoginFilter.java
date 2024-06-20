package com.nhnacademy.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.auth.domain.CustomUserDetails;
import com.nhnacademy.auth.domain.RefreshEntity;
import com.nhnacademy.auth.dto.ClientLoginRequestDto;
import com.nhnacademy.auth.utils.JWTUtils;
import com.nhnacademy.auth.utils.RedisUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private  final RedisTemplate<String, Object> redisTemplate;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtils jwtUtils, RedisTemplate<String, Object> redisTemplate) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        ClientLoginRequestDto req;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            req = objectMapper.readValue(StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8), ClientLoginRequestDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(req.getClientEmail(), req.getClientPassword(), null);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();

        String email = customUserDetails.getUsername();
        String name = customUserDetails.getResponseDto().getClientName();
        String role = authResult.getAuthorities().iterator().next().getAuthority();
        String refresh = jwtUtils.createRefreshToken(email, name, role);

        addRefreshToken(email, name, role, refresh);

        response.addHeader("access", jwtUtils.createAccessToken(email, name, role));
        response.addHeader("refresh", refresh);
        response.setHeader("name", name);
        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(401);
    }

    public void addRefreshToken(String email, String name, String role, String refresh) {
        redisTemplate.opsForHash().put(refresh, RedisUtils.getTokenPrefix(), RefreshEntity.builder()
                .email(email)
                .name(name)
                .role(role)
                .build());
        redisTemplate.expire(refresh, 14, TimeUnit.DAYS);
    }
}
