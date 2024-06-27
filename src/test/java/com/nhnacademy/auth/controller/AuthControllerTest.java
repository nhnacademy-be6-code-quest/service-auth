package com.nhnacademy.auth.controller;

import com.nhnacademy.auth.config.SecurityConfig;
import com.nhnacademy.auth.dto.request.ClientLoginRequestDto;
import com.nhnacademy.auth.dto.response.TokenResponseDto;
import com.nhnacademy.auth.exception.LoginFailException;
import com.nhnacademy.auth.exception.TokenInvalidationException;
import com.nhnacademy.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Import(SecurityConfig.class)
@WebMvcTest(AuthControllerImp.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    private TokenResponseDto tokenResponseDto;

    @BeforeEach
    void setUp() {
        tokenResponseDto = new TokenResponseDto("accessToken", "refreshToken");
    }

    @Test
    void testReissue() throws Exception {
        when(authService.reissue(any())).thenReturn(tokenResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/reissue")
                        .header("refresh", "valid_refresh_token"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.access").value("accessToken"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refresh").value("refreshToken"))
                .andDo(print());
    }

    @Test
    void testLogin() throws Exception {
        when(authService.login(any(), any())).thenReturn(tokenResponseDto);

        ClientLoginRequestDto requestDto = new ClientLoginRequestDto("test@example.com", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clientEmail\":\"test@example.com\",\"clientPassword\":\"password\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.access").value("accessToken"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refresh").value("refreshToken"))
                .andDo(print());
    }

    @Test
    void testLogout() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/logout")
                        .header("refresh", "valid_refresh_token"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    void testHandleTokenInvalidationException() throws Exception {
        doThrow(new TokenInvalidationException("Invalid token")).when(authService).reissue(any());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/reissue")
                        .header("refresh", "invalid_refresh_token"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andDo(print());
    }

    @Test
    void testHandleLoginFailException() throws Exception {
        doThrow(new LoginFailException("Login failed")).when(authService).login(any(), any());

        ClientLoginRequestDto requestDto = new ClientLoginRequestDto("test@example.com", "wrong_password");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clientEmail\":\"test@example.com\",\"clientPassword\":\"wrong_password\"}"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andDo(print());
    }
}
