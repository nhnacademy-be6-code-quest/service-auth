package com.nhnacademy.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SwaggerConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testCustomOpenAPIBean() {
        OpenAPI openAPI = applicationContext.getBean(OpenAPI.class);

        assertThat(openAPI).isNotNull();

        Info info = openAPI.getInfo();
        assertThat(info).isNotNull();
        assertThat(info.getTitle()).isEqualTo("Auth API");
        assertThat(info.getVersion()).isEqualTo("1.0");
        assertThat(info.getDescription()).isEqualTo("Auth API");
    }
}
