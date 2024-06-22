package com.nhnacademy.auth.filter;

import com.nhnacademy.auth.utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class LogoutFilterTest {

    @InjectMocks
    private LogoutFilter logoutFilter;

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        logoutFilter = new LogoutFilter(jwtUtils, redisTemplate);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    }

    @Test
    public void testDoFilter_NotLogoutPath() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/not-logout");
        MockHttpServletResponse response = new MockHttpServletResponse();

        logoutFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void testDoFilter_LogoutPath_NoRefreshToken() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/logout");
        MockHttpServletResponse response = new MockHttpServletResponse();

        logoutFilter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, times(0)).doFilter(request, response);
    }

    @Test
    public void testDoFilter_LogoutPath_InvalidToken() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/logout");
        request.addHeader("refresh", "invalidToken");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtils.isExpired(anyString())).thenReturn(false);
        when(jwtUtils.getCategory(anyString())).thenReturn("invalidCategory");

        logoutFilter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, times(0)).doFilter(request, response);
    }

    @Test
    public void testDoFilter_LogoutPath_ExpiredToken() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/logout");
        request.addHeader("refresh", "expiredToken");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtils.isExpired(anyString())).thenReturn(true);

        logoutFilter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, times(0)).doFilter(request, response);
    }

    @Test
    public void testDoFilter_LogoutPath_ValidToken() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/logout");
        request.addHeader("refresh", "validToken");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtils.isExpired(anyString())).thenReturn(false);
        when(jwtUtils.getCategory(anyString())).thenReturn("refresh");
        when(jwtUtils.getUUID(anyString())).thenReturn("uuid");
        when(hashOperations.get(anyString(), anyString())).thenReturn("test@example.com");

        logoutFilter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
        verify(redisTemplate, times(1)).delete(anyString());
        verify(filterChain, times(0)).doFilter(request, response);
    }

    @Test
    public void testDoFilter_LogoutPath_InvalidUUID() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/logout");
        request.addHeader("refresh", "validToken");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtils.isExpired(anyString())).thenReturn(false);
        when(jwtUtils.getCategory(anyString())).thenReturn("refresh");
        when(jwtUtils.getUUID(anyString())).thenReturn("uuid");
        when(hashOperations.get(anyString(), anyString())).thenReturn(null);

        logoutFilter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        verify(redisTemplate, times(0)).delete(anyString());
        verify(filterChain, times(0)).doFilter(request, response);
    }
}
