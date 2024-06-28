package com.nhnacademy.auth.controller;

import com.nhnacademy.auth.dto.request.ClientLoginRequestDto;
import com.nhnacademy.auth.dto.response.TokenResponseDto;
import com.nhnacademy.auth.exception.LoginFailException;
import com.nhnacademy.auth.exception.TokenInvalidationException;
import com.nhnacademy.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthControllerImp implements AuthController {
    private final AuthService authService;

    @Override
    @PostMapping("/api/reissue")
    public ResponseEntity<TokenResponseDto> reissue(@RequestHeader("refresh") String refresh) {
        log.info("reissue");
        return new ResponseEntity<>(authService.reissue(refresh), HttpStatus.OK);
    }

    @Override
    @PostMapping("/api/login")
    public ResponseEntity<TokenResponseDto> login(ClientLoginRequestDto clientLoginRequestDto) {
        log.info("login");
        return new ResponseEntity<>(authService.login(clientLoginRequestDto.getClientEmail(), clientLoginRequestDto.getClientPassword()), HttpStatus.OK);
    }

    @Override
    @PostMapping("/api/logout")
    public ResponseEntity<String> logout(HttpHeaders headers) {
        log.info("logout");
        return new ResponseEntity<>(authService.logout(headers.getFirst("refresh")), HttpStatus.OK);
    }

    @ExceptionHandler(TokenInvalidationException.class)
    public ResponseEntity<String> handleTokenInvalidationException(TokenInvalidationException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(LoginFailException.class)
    public ResponseEntity<String> handleLoginFailException(LoginFailException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}