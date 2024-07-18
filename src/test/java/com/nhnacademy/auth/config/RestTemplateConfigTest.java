package com.nhnacademy.auth.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class RestTemplateConfigTest {

    @Test
    void testRestTemplateCreation() {
        RestTemplateConfig config = new RestTemplateConfig();
        RestTemplate restTemplate = config.restTemplate();

        assertNotNull(restTemplate);
        assertTrue(restTemplate instanceof RestTemplate);
    }

    @Test
    void testRestTemplateIsNewInstance() {
        RestTemplateConfig config = new RestTemplateConfig();
        RestTemplate firstInstance = config.restTemplate();
        RestTemplate secondInstance = config.restTemplate();

        assertNotNull(firstInstance);
        assertNotNull(secondInstance);
        assertNotSame(firstInstance, secondInstance, "Each call should return a new instance");
    }

    @Test
    void testRestTemplateHasDefaultConfiguration() {
        RestTemplateConfig config = new RestTemplateConfig();
        RestTemplate restTemplate = config.restTemplate();

        assertNotNull(restTemplate.getMessageConverters());
        assertFalse(restTemplate.getMessageConverters().isEmpty());
        assertNotNull(restTemplate.getErrorHandler());
        // 기본 구성에 대한 추가 검증을 여기에 추가할 수 있습니다.
    }
}