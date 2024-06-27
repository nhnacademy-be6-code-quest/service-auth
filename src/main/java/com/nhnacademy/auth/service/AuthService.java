package com.nhnacademy.auth.service;

import com.nhnacademy.auth.dto.response.TokenResponseDto;

public interface AuthService {
    /**
     * 토큰 재발급시 호출되는 api 함수로 refresh 토큰을 받아 유효한지 판단후 access, refresh 토큰을 재발급합니다.
     *
     * @author gihwanJang
     * @param refresh 재발급을 하기위한 토큰
     * @return 유효하다면 access, refresh 토큰을 유효하지 않다면 TokenInvalidation 예외를 반환합니다.
     */
    TokenResponseDto reissue(String refresh);

    /**
     * 로그인시 호출되는 api 함수로 email과 password를 인자로 받아 일치하는지 판단후 토큰을 발급합니다.
     *
     * @author gihwanJang
     * @param clientEmail 사용자가 입력한 email
     * @param clientPassword 사용자가 입력한 password
     * @return 유효하다면 access, refresh 토큰을 유효하지 않다면 LoginFail 예외를 반환합니다.
     */
    TokenResponseDto login(String clientEmail, String clientPassword);

    /**
     * 로그아웃시 호출되는 api 함수로 refresh 토근을 받아 유효하다면 redis에서 삭제합니다.
     *
     * @author gihwanJang
     * @param refresh 로그아웃을 위한 토큰
     * @return 성공여부 반환
     */
    String logout(String refresh);
}
