package com.nhnacademy.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SwaggerConfigTest {

    @Test
    void testCustomOpenAPI() {
        SwaggerConfig config = new SwaggerConfig();
        OpenAPI openAPI = config.customOpenAPI();

        assertNotNull(openAPI);
        assertNotNull(openAPI.getComponents());
        assertNotNull(openAPI.getInfo());

        Info info = openAPI.getInfo();
        assertEquals("Auth API", info.getTitle());
        assertEquals("1.0", info.getVersion());
        assertEquals("Auth API", info.getDescription());
    }

    @Test
    void testOpenAPIComponentsAreEmpty() {
        SwaggerConfig config = new SwaggerConfig();
        OpenAPI openAPI = config.customOpenAPI();

        assertNotNull(openAPI.getComponents());
        assertTrue(openAPI.getComponents().getSchemas() == null || openAPI.getComponents().getSchemas().isEmpty());
        assertTrue(openAPI.getComponents().getSecuritySchemes() == null || openAPI.getComponents().getSecuritySchemes().isEmpty());
    }

    @Test
    void testOpenAPIInfoNotNull() {
        SwaggerConfig config = new SwaggerConfig();
        OpenAPI openAPI = config.customOpenAPI();

        assertNotNull(openAPI.getInfo());
        assertNotNull(openAPI.getInfo().getTitle());
        assertNotNull(openAPI.getInfo().getVersion());
        assertNotNull(openAPI.getInfo().getDescription());
    }
}