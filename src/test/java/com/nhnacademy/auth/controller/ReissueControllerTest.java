package com.nhnacademy.auth.controller;

import com.nhnacademy.auth.utils.JWTUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReissueController.class)
class ReissueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JWTUtils jwtUtils;

    @MockBean
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @BeforeEach
    public void setUp() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    }

    @Test
    @WithMockUser
    public void testReissue_NoRefreshToken() throws Exception {
        mockMvc.perform(post("/reissue")
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void testReissue_ExpiredRefreshToken() throws Exception {
        String expiredToken = "expiredToken";

        when(jwtUtils.isExpired(expiredToken)).thenReturn(true);

        mockMvc.perform(post("/reissue")
                        .with(csrf())
                        .header("refresh", expiredToken))
                .andExpect(status().isUnauthorized());

        verify(jwtUtils, times(1)).isExpired(expiredToken);
    }

    @Test
    @WithMockUser
    public void testReissue_InvalidRefreshToken() throws Exception {
        String validToken = "validToken";
        String uuid = UUID.randomUUID().toString();

        when(jwtUtils.isExpired(validToken)).thenReturn(false);
        when(jwtUtils.getUUID(validToken)).thenReturn(uuid);
        when(hashOperations.get(validToken, uuid)).thenReturn(null);

        mockMvc.perform(post("/reissue")
                        .with(csrf())
                        .header("refresh", validToken))
                .andExpect(status().isUnauthorized());

        verify(jwtUtils, times(1)).isExpired(validToken);
        verify(hashOperations, times(1)).get(validToken, uuid);
    }

    @Test
    @WithMockUser
    public void testReissue_ValidRefreshToken() throws Exception {
        String validToken = "validToken";
        Long id = 1L;
        String uuid = UUID.randomUUID().toString();
        String newUuid = UUID.randomUUID().toString();
        String role = "ROLE_USER";
        String newRefreshToken = "newRefreshToken";
        String newAccessToken = "newAccessToken";

        when(jwtUtils.isExpired(validToken)).thenReturn(false);
        when(jwtUtils.getUUID(validToken)).thenReturn(uuid);
        when(hashOperations.get(validToken, uuid)).thenReturn(id);
        when(jwtUtils.getRole(validToken)).thenReturn(role);
        when(jwtUtils.createRefreshToken(anyString(), anyString())).thenReturn(newRefreshToken);
        when(jwtUtils.createAccessToken(anyString(), anyString())).thenReturn(newAccessToken);

        mockMvc.perform(post("/reissue")
                        .with(csrf())
                        .header("refresh", validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.access").value(newAccessToken))
                .andExpect(jsonPath("$.refresh").value(newRefreshToken));

        verify(redisTemplate, times(1)).delete(validToken);
        verify(redisTemplate, times(1)).expire(newRefreshToken, 14, TimeUnit.DAYS);
    }
}