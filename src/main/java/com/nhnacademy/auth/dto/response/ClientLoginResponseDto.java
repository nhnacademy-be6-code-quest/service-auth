package com.nhnacademy.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ClientLoginResponseDto {
    private List<String> role;
    private Long clientId;
    private String clientEmail;
    private String clientPassword;
    private String clientName;
}
