package com.nhnacademy.auth.dto.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenResponseDtoTest {

    @Test
    void testAllArgsConstructor() {
        TokenResponseDto dto = new TokenResponseDto("accessToken", "refreshToken");
        assertThat(dto.getAccess()).isEqualTo("accessToken");
        assertThat(dto.getRefresh()).isEqualTo("refreshToken");
    }

    @Test
    void testSettersAndGetters() {
        TokenResponseDto dto = new TokenResponseDto("accessToken", "refreshToken");
        dto.setAccess("newAccessToken");
        dto.setRefresh("newRefreshToken");

        assertThat(dto.getAccess()).isEqualTo("newAccessToken");
        assertThat(dto.getRefresh()).isEqualTo("newRefreshToken");
    }

    @Test
    void testToString() {
        TokenResponseDto dto = new TokenResponseDto("accessToken", "refreshToken");
        assertThat(dto.toString()).hasToString("TokenResponseDto(access=accessToken, refresh=refreshToken)");
    }

    @Test
    void testEqualsAndHashCode() {
        TokenResponseDto dto1 = new TokenResponseDto("accessToken", "refreshToken");
        TokenResponseDto dto2 = new TokenResponseDto("accessToken", "refreshToken");
        TokenResponseDto dto3 = new TokenResponseDto("differentAccessToken", "differentRefreshToken");

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1).isNotEqualTo(dto3);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1.hashCode()).isNotEqualTo(dto3.hashCode());
    }
}
