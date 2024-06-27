package com.nhnacademy.auth.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ClientLoginResponseDto {
    private Role role;
    private Long clientId;
    private String clientEmail;
    private String clientPassword;
    private String clientName;
}
