package com.nhnacademy.auth.dto;

import com.nhnacademy.auth.dto.response.TokenResponseDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenResponseDtoTest {

    @Test
    public void testTokenResponseDto() {
        String accessToken = "access_token_example";
        String refreshToken = "refresh_token_example";

        TokenResponseDto tokenResponseDto = new TokenResponseDto(accessToken, refreshToken);

        assertThat(tokenResponseDto.getAccess()).isEqualTo(accessToken);
        assertThat(tokenResponseDto.getRefresh()).isEqualTo(refreshToken);

        String newAccessToken = "new_access_token_example";
        String newRefreshToken = "new_refresh_token_example";

        tokenResponseDto.setAccess(newAccessToken);
        tokenResponseDto.setRefresh(newRefreshToken);

        assertThat(tokenResponseDto.getAccess()).isEqualTo(newAccessToken);
        assertThat(tokenResponseDto.getRefresh()).isEqualTo(newRefreshToken);
    }

    @Test
    public void testTokenResponseDtoEqualsAndHashCode() {
        String accessToken = "access_token_example";
        String refreshToken = "refresh_token_example";

        TokenResponseDto tokenResponseDto1 = new TokenResponseDto(accessToken, refreshToken);
        TokenResponseDto tokenResponseDto2 = new TokenResponseDto(accessToken, refreshToken);

        assertThat(tokenResponseDto1).isEqualTo(tokenResponseDto2);
        assertThat(tokenResponseDto1.hashCode()).isEqualTo(tokenResponseDto2.hashCode());

        TokenResponseDto tokenResponseDto3 = new TokenResponseDto("different_access_token", "different_refresh_token");

        assertThat(tokenResponseDto1).isNotEqualTo(tokenResponseDto3);
        assertThat(tokenResponseDto1.hashCode()).isNotEqualTo(tokenResponseDto3.hashCode());
    }

    @Test
    public void testTokenResponseDtoToString() {
        String accessToken = "access_token_example";
        String refreshToken = "refresh_token_example";

        TokenResponseDto tokenResponseDto = new TokenResponseDto(accessToken, refreshToken);

        String expectedString = "TokenResponseDto(access=" + accessToken + ", refresh=" + refreshToken + ")";
        assertThat(tokenResponseDto.toString()).isEqualTo(expectedString);
    }
}
