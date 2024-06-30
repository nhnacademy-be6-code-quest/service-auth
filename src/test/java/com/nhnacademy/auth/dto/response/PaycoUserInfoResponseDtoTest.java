package com.nhnacademy.auth.dto.response;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PaycoUserInfoResponseDtoTest {

    @Test
    void testNoArgsConstructor() {
        PaycoUserInfoResponseDto dto = new PaycoUserInfoResponseDto();
        assertThat(dto.getHeader()).isNull();
        assertThat(dto.getData()).isNull();
    }

    @Test
    void testSettersAndGetters() {
        PaycoUserInfoResponseDto dto = new PaycoUserInfoResponseDto();

        PaycoUserInfoResponseDto.Header header = new PaycoUserInfoResponseDto.Header();
        header.setSuccessful(true);
        header.setResultCode(200);
        header.setResultMessage("Success");

        PaycoUserInfoResponseDto.Data data = new PaycoUserInfoResponseDto.Data();
        PaycoUserInfoResponseDto.Member member = new PaycoUserInfoResponseDto.Member();
        member.setIdNo("idNo");
        member.setEmail("email@example.com");
        member.setMaskedEmail("masked@example.com");
        member.setName("John Doe");
        member.setGenderCode("M");
        member.setBirthdayMMdd("0101");
        member.setAgeGroup("20-29");
        data.setMember(member);

        dto.setHeader(header);
        dto.setData(data);

        assertThat(dto.getHeader()).isEqualTo(header);
        assertThat(dto.getData()).isEqualTo(data);
        assertThat(dto.getHeader().isSuccessful()).isTrue();
        assertThat(dto.getHeader().getResultCode()).isEqualTo(200);
        assertThat(dto.getHeader().getResultMessage()).isEqualTo("Success");
        assertThat(dto.getData().getMember()).isEqualTo(member);
        assertThat(dto.getData().getMember().getIdNo()).isEqualTo("idNo");
        assertThat(dto.getData().getMember().getEmail()).isEqualTo("email@example.com");
        assertThat(dto.getData().getMember().getMaskedEmail()).isEqualTo("masked@example.com");
        assertThat(dto.getData().getMember().getName()).isEqualTo("John Doe");
        assertThat(dto.getData().getMember().getGenderCode()).isEqualTo("M");
        assertThat(dto.getData().getMember().getBirthdayMMdd()).isEqualTo("0101");
        assertThat(dto.getData().getMember().getAgeGroup()).isEqualTo("20-29");
    }
}
