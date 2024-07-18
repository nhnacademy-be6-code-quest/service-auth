package com.nhnacademy.auth.utils;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Setter
@Component
@RequiredArgsConstructor
public class TransformerUtils {
    private String clientEncodingKey;

    public String encode(String input) {
        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = clientEncodingKey.getBytes(StandardCharsets.UTF_8);
        byte[] outputBytes = new byte[inputBytes.length];

        for (int i = 0; i < inputBytes.length; ++i) {
            outputBytes[i] = (byte) (inputBytes[i] ^ keyBytes[i % keyBytes.length]);
        }

        return Base64.getEncoder().encodeToString(outputBytes);
    }

    public String decode(String input) {
        byte[] inputBytes = Base64.getDecoder().decode(input);
        byte[] keyBytes = clientEncodingKey.getBytes(StandardCharsets.UTF_8);
        byte[] outputBytes = new byte[inputBytes.length];

        for (int i = 0; i < inputBytes.length; i++) {
            outputBytes[i] = (byte) (inputBytes[i] ^ keyBytes[i % keyBytes.length]);
        }

        return new String(outputBytes, StandardCharsets.UTF_8);
    }
}
