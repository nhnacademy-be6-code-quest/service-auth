package com.nhnacademy.auth.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class RestTemplateConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testRestTemplateBeanCreation() {
        RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
        assertThat(restTemplate).isNotNull();
    }
}
