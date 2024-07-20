package com.nhnacademy.auth.dto.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaycoOAuthResponseDtoTest {

    @Test
    void testNoArgsConstructor() {
        PaycoOAuthResponseDto dto = new PaycoOAuthResponseDto();
        assertThat(dto.getAccessTokenSecret()).isNull();
        assertThat(dto.getState()).isNull();
        assertThat(dto.getTokenType()).isNull();
        assertThat(dto.getExpiresIn()).isNull();
        assertThat(dto.getAccessToken()).isNull();
        assertThat(dto.getRefreshToken()).isNull();
    }

    @Test
    void testSettersAndGetters() {
        PaycoOAuthResponseDto dto = new PaycoOAuthResponseDto();
        dto.setAccessTokenSecret("accessTokenSecret");
        dto.setState("state");
        dto.setTokenType("tokenType");
        dto.setExpiresIn("expiresIn");
        dto.setAccessToken("accessToken");
        dto.setRefreshToken("refreshToken");

        assertThat(dto.getAccessTokenSecret()).isEqualTo("accessTokenSecret");
        assertThat(dto.getState()).isEqualTo("state");
        assertThat(dto.getTokenType()).isEqualTo("tokenType");
        assertThat(dto.getExpiresIn()).isEqualTo("expiresIn");
        assertThat(dto.getAccessToken()).isEqualTo("accessToken");
        assertThat(dto.getRefreshToken()).isEqualTo("refreshToken");
    }
}
