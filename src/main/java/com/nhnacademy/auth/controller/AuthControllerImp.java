package com.nhnacademy.auth.controller;

import com.nhnacademy.auth.dto.request.ClientLoginRequestDto;
import com.nhnacademy.auth.dto.request.OAuthRegisterRequestDto;
import com.nhnacademy.auth.dto.response.TokenResponseDto;
import com.nhnacademy.auth.exception.DeletedClientException;
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
    public ResponseEntity<TokenResponseDto> reissue(HttpHeaders headers) {
        log.info("reissue");
        return new ResponseEntity<>(authService.reissue(headers.getFirst("refresh"), headers.getFirst("access")), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TokenResponseDto> login(ClientLoginRequestDto clientLoginRequestDto) {
        log.info("login");
        return new ResponseEntity<>(authService.login(clientLoginRequestDto.getClientEmail(), clientLoginRequestDto.getClientPassword()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> logout(HttpHeaders headers) {
        log.info("logout");
        return new ResponseEntity<>(authService.logout(headers.getFirst("refresh"), headers.getFirst("access")), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TokenResponseDto> paycoLoginCallback(String code) {
        log.info("payco login callback");
        return ResponseEntity.ok(authService.paycoOAuthLogin(code));
    }

    @Override
    public ResponseEntity<String> paycoRecoveryCallback(String code) {
        log.info("payco recovery callback");
        return ResponseEntity.ok(authService.paycoOAuthRecovery(code));
    }

    @Override
    public ResponseEntity<TokenResponseDto> oAuthRegister(OAuthRegisterRequestDto oAuthRegisterRequestDto) {
        log.info("oAuthRegister");
        return ResponseEntity.ok(authService.oAuthRegister(
                        oAuthRegisterRequestDto.getAccess(),
                        oAuthRegisterRequestDto.getName(),
                        oAuthRegisterRequestDto.getBirth()
                )
        );
    }

    @Override
    public ResponseEntity<String> handleTokenInvalidationException(TokenInvalidationException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<String> handleLoginFailException(LoginFailException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<String> handleDeletedClientException(DeletedClientException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(HttpStatus.GONE);
    }
}