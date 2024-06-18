package com.nhnacademy.auth.dto;

import com.nhnacademy.auth.domain.Role;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ClientLoginResponseDto {
    private Role role;
    private String clientEmail;
    private String clientPassword;
    private String clientName;
}
