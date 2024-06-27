package com.nhnacademy.auth.client;

import com.nhnacademy.auth.dto.response.ClientLoginResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "client", url = "http://localhost:8001")
public interface Client {
    @GetMapping("/api/client/login")
    ResponseEntity<ClientLoginResponseDto> login(@RequestParam String email);
}
