package com.nhnacademy.auth.controller;

import com.nhnacademy.auth.dto.request.ClientLoginRequestDto;
import com.nhnacademy.auth.dto.request.OAuthRegisterRequestDto;
import com.nhnacademy.auth.dto.response.TokenResponseDto;
import com.nhnacademy.auth.exception.LoginFailException;
import com.nhnacademy.auth.exception.TokenInvalidationException;
import com.nhnacademy.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthControllerImp implements AuthController {
    private final AuthService authService;

    @Override
    @PostMapping("/api/reissue")
    public ResponseEntity<TokenResponseDto> reissue(@RequestHeader HttpHeaders headers) {
        log.info("reissue");
        return new ResponseEntity<>(authService.reissue(headers.getFirst("refresh"), headers.getFirst("access")), HttpStatus.OK);
    }

    @Override
    @PostMapping("/api/login")
    public ResponseEntity<TokenResponseDto> login(ClientLoginRequestDto clientLoginRequestDto) {
        log.info("login");
        return new ResponseEntity<>(authService.login(clientLoginRequestDto.getClientEmail(), clientLoginRequestDto.getClientPassword()), HttpStatus.OK);
    }

    @Override
    @PostMapping("/api/logout")
    public ResponseEntity<String> logout(@RequestHeader  HttpHeaders headers) {
        log.info("logout");
        return new ResponseEntity<>(authService.logout(headers.getFirst("refresh")), HttpStatus.OK);
    }

    @Override
    @GetMapping("/api/payco/login/callback")
    public ResponseEntity<TokenResponseDto> paycoLoginCallback(@RequestParam("code") String code) {
        log.info("payco login callback");
        return ResponseEntity.ok(authService.paycoOAuthLogin(code));
    }

    @Override
    @GetMapping("/api/payco/recovery/callback")
    public ResponseEntity<String> paycoRecoveryCallback(@RequestParam("code") String code) {
        log.info("payco recovery callback");
        return ResponseEntity.ok(authService.paycoOAuthRecovery(code));
    }

    @Override
    @PostMapping("/api/oauth")
    public ResponseEntity<TokenResponseDto> oAuthRegister(@RequestBody OAuthRegisterRequestDto oAuthRegisterRequestDto) {
        log.info("oAuthRegister");
        return ResponseEntity.ok(authService.oAuthRegister(
                        oAuthRegisterRequestDto.getAccess(),
                        oAuthRegisterRequestDto.getName(),
                        oAuthRegisterRequestDto.getBirth()
                )
        );
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