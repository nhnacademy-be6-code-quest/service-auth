package com.nhnacademy.auth.dto.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientOAuthRegisterRequestDto {
    private String identify;
    private String name;
    private LocalDate birth;
}
