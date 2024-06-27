package com.nhnacademy.auth.controller;

import com.nhnacademy.auth.dto.request.ClientLoginRequestDto;
import com.nhnacademy.auth.dto.response.TokenResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Auth", description = "인증 관련 API")
public interface AuthController {
    @Operation(
            summary = "토큰 재발급",
            description = "EveryWhere - 토큰 만료시 Refresh 토큰을 이용한 재발급",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "재발급된 access 토큰 반환"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패시 반환"
                    )
            }
    )
    @PostMapping("/api/reissue")
    ResponseEntity<TokenResponseDto> reissue(@RequestHeader("refresh") String refresh);

    @Operation(
            summary = "로그인",
            description = "auth - 로그인",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "access, refresh 토큰 반환"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패시 반환"
                    )
            }
    )
    @PostMapping("/api/login")
    ResponseEntity<TokenResponseDto> login(@RequestBody ClientLoginRequestDto clientLoginRequestDto);

    @Operation(
            summary = "로그아웃",
            description = "auth - 로그아웃",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공 반환"
                    ),
            }
    )
    @PostMapping("/api/logout")
    ResponseEntity<String> logout(@RequestHeader HttpHeaders headers);
}