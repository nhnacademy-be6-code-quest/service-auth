package com.nhnacademy.auth.controller;

import com.nhnacademy.auth.config.SecurityConfig;
import com.nhnacademy.auth.dto.request.ClientLoginRequestDto;
import com.nhnacademy.auth.dto.request.OAuthRegisterRequestDto;
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

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Import(SecurityConfig.class)
@WebMvcTest(AuthControllerImp.class)
class AuthControllerTest {

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
        when(authService.reissue(anyString())).thenReturn(tokenResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/reissue")
                        .header("refresh", "valid_refresh_token"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.access").value("accessToken"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refresh").value("refreshToken"))
                .andDo(print());
    }

    @Test
    void testLogin() throws Exception {
        when(authService.login(anyString(), anyString())).thenReturn(tokenResponseDto);

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
        doThrow(new TokenInvalidationException("Invalid token")).when(authService).reissue(anyString());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/reissue")
                        .header("refresh", "invalid_refresh_token"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andDo(print());
    }

    @Test
    void testHandleLoginFailException() throws Exception {
        doThrow(new LoginFailException("Login failed")).when(authService).login(anyString(), anyString());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clientEmail\":\"test@example.com\",\"clientPassword\":\"wrong_password\"}"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andDo(print());
    }

    @Test
    void testPaycoLoginCallback() throws Exception {
        when(authService.paycoOAuthLogin(anyString())).thenReturn(tokenResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/payco/login/callback")
                        .param("code", "valid_code"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.access").value("accessToken"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refresh").value("refreshToken"))
                .andDo(print());
    }

    @Test
    void testOAuthRegister() throws Exception {
        when(authService.oAuthRegister(anyString(), anyString(), any())).thenReturn(tokenResponseDto);

        OAuthRegisterRequestDto requestDto = new OAuthRegisterRequestDto("access", "name", LocalDate.now());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/oauth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"access\":\"access\",\"name\":\"name\",\"birth\":\"1999-03-19\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.access").value("accessToken"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refresh").value("refreshToken"))
                .andDo(print());
    }
}
