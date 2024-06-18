package com.nhnacademy.auth.service;

import com.nhnacademy.auth.client.Client;
import com.nhnacademy.auth.domain.CustomUserDetails;
import com.nhnacademy.auth.dto.ClientLoginResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final Client client;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        ClientLoginResponseDto response = client.login(email).getBody();
        if (response != null) {
            return new CustomUserDetails(response);
        }
        return null;
    }
}
