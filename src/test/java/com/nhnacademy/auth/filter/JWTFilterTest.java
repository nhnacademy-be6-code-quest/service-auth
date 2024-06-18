package com.nhnacademy.auth.filter;

import com.nhnacademy.auth.domain.CustomUserDetails;
import com.nhnacademy.auth.domain.Role;
import com.nhnacademy.auth.dto.ClientLoginResponseDto;
import com.nhnacademy.auth.utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class JWTFilterTest {

    @InjectMocks
    private JWTFilter jwtFilter;

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDoFilterInternalWithValidToken() throws ServletException, IOException {
        String token = "validToken";
        String authorizationHeader = "Bearer " + token;
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", authorizationHeader);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtils.isExpired(token)).thenReturn(false);
        when(jwtUtils.getUserEmail(token)).thenReturn("test@example.com");
        when(jwtUtils.getUserName(token)).thenReturn("Test User");
        when(jwtUtils.getRole(token)).thenReturn("ROLE_USER");

        jwtFilter.doFilterInternal(request, response, filterChain);

        CustomUserDetails expectedUserDetails = new CustomUserDetails(ClientLoginResponseDto.builder()
                .clientEmail("test@example.com")
                .clientName("Test User")
                .role(Role.ROLE_USER)
                .build());

        Authentication expectedAuthToken = new UsernamePasswordAuthenticationToken(expectedUserDetails, null, expectedUserDetails.getAuthorities());

        Authentication actualAuthToken = SecurityContextHolder.getContext().getAuthentication();

        assertThat(actualAuthToken).isNotNull();
        assertThat(actualAuthToken.getName()).isEqualTo(expectedAuthToken.getName());
        assertThat(actualAuthToken.getAuthorities()).hasSize(1);
        assertThat(actualAuthToken.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");

        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternalWithExpiredToken() throws ServletException, IOException {
        String token = "expiredToken";
        String authorizationHeader = "Bearer " + token;
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", authorizationHeader);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtils.isExpired(token)).thenReturn(true);

        jwtFilter.doFilterInternal(request, response, filterChain);

        Authentication actualAuthToken = SecurityContextHolder.getContext().getAuthentication();
        assertThat(actualAuthToken).isNull();

        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternalWithoutAuthorizationHeader() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtFilter.doFilterInternal(request, response, filterChain);

        Authentication actualAuthToken = SecurityContextHolder.getContext().getAuthentication();
        assertThat(actualAuthToken).isNull();

        verify(filterChain).doFilter(request, response);
    }
}
