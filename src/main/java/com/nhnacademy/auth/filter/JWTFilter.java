package com.nhnacademy.auth.filter;

import com.nhnacademy.auth.domain.CustomUserDetails;
import com.nhnacademy.auth.domain.Role;
import com.nhnacademy.auth.dto.ClientLoginResponseDto;
import com.nhnacademy.auth.utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtils jwtUtils;

    public JWTFilter(JWTUtils jwtUtil) {
        this.jwtUtils = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization= request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.split(" ")[1];

        if (jwtUtils.isExpired(token)) {
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }
        CustomUserDetails customUserDetails = new CustomUserDetails(ClientLoginResponseDto.builder()
                .clientEmail(jwtUtils.getUserEmail(token))
                .clientName(jwtUtils.getUserName(token))
                .role(Role.valueOf(jwtUtils.getRole(token)))
                .build());

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}
