package com.nhnacademy.auth.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientLoginMessageDto implements Serializable {
    private Long clientId;
    private LocalDateTime lastLoginDate;
}
