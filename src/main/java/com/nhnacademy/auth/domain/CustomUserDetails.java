package com.nhnacademy.auth.domain;

import com.nhnacademy.auth.dto.ClientLoginResponseDto;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {
    private final ClientLoginResponseDto responseDto;

    public CustomUserDetails(ClientLoginResponseDto responseDto) {
        this.responseDto = responseDto;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(responseDto.getRole());
    }

    @Override
    public String getPassword() {
        return responseDto.getClientPassword();
    }

    @Override
    public String getUsername() {
        return responseDto.getClientEmail();
    }
}
