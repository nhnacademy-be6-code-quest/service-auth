package com.nhnacademy.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class PaycoUserInfoResponseDto {
    private Header header;
    private Data data;


    @Getter
    @Setter
    @NoArgsConstructor
    public static class Header {
        @JsonProperty("isSuccessful")
        private boolean isSuccessful;
        private int resultCode;
        private String resultMessage;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Data {
        private Member member;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Member {
        private String idNo;
        private String email;
        private String maskedEmail;
        private String name;
        private String genderCode;
        private String birthdayMMdd;
        private String ageGroup;
    }
}
