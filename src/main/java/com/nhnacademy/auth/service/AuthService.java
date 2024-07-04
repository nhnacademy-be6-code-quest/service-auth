package com.nhnacademy.auth.service;

import com.nhnacademy.auth.dto.response.TokenResponseDto;

import java.time.LocalDate;

public interface AuthService {
    /**
     * 토큰 재발급시 호출되는 api 함수로 refresh 토큰을 받아 유효한지 판단후 access, refresh 토큰을 재발급합니다.
     *
     * @author gihwanJang
     * @param refresh 재발급을 하기위한 토큰
     * @return 유효하다면 access, refresh 토큰을 유효하지 않다면 TokenInvalidation 예외를 반환합니다.
     */
    TokenResponseDto reissue(String refresh, String access);

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
    String logout(String refresh, String access);

    /**
     * 휴면처리된 Oauth 계정을 복구할 때 호출되는 api 함수
     *
     * @author gihwanJang
     * @param code 로그인 후 발급받은 코드
     * @return 유저 id 정보를 반환
     */
    String paycoOAuthRecovery(String code);

    /**
     * PaycoOAuth 로그인시 호출 되는 api 함수
     * 로그인시 지급받은 code를 이용하여 access 토큰을 발급 받고 해당 토큰을 이용하여 사용자정보를 얻어옵니다. </br>
     * 해당 정보가 회원으로 등록되어 있다면 Access 토큰과 Refresh 토큰을 발행하고 로그인 처리합니다. </br>
     * 등록 되어있지 않다면 Access 토큰만 발행합니다.
     *
     * @author gihwanJang
     * @param code 로그인 후 발급받은 코드
     * @return 토큰 정보를 반환
     */
    TokenResponseDto paycoOAuthLogin(String code);

    /**
     * 처음 로그인한 PaycoOAuth 유저를 등록할 때 호출되는 api 함수
     *
     * @author gihwanJang
     * @param access accessToken
     * @param name 유저 이름
     * @param birth 유저 생일
     * @return 토큰 정보를 반환
     */
    TokenResponseDto oAuthRegister(String access, String name, LocalDate birth);
}
