package com.nhnacademy.auth.filter;

import com.nhnacademy.auth.utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class LogoutFilter extends GenericFilterBean {
    private final JWTUtils jwtUtils;
    private final RedisTemplate<String, Object> redisTemplate;

    public LogoutFilter(JWTUtils jwtUtils, RedisTemplate<String, Object> redisTemplate) {
        this.jwtUtils = jwtUtils;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (!(request.getRequestURI().matches("^\\/logout$") && request.getMethod().equals("POST"))) {
            filterChain.doFilter(request, response);
            return;
        }

        String refresh = request.getHeader("refresh");
        if (refresh == null || jwtUtils.isExpired(refresh) || !jwtUtils.getCategory(refresh).equals("refresh")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String rf = String.valueOf(redisTemplate.opsForHash().get(refresh, jwtUtils.getUUID(refresh)));
        if (rf == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        redisTemplate.delete(refresh);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
