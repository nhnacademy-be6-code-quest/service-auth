package com.nhnacademy.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientLoginRequestDto {
    private String clientEmail;
    private String clientPassword;
}
