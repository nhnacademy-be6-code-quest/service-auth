package com.nhnacademy.auth.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RedisTemplateConfigTest {

    @Mock
    private RedisConnectionFactory mockRedisConnectionFactory;

    private RedisTemplateConfig redisTemplateConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        redisTemplateConfig = new RedisTemplateConfig(Map.of(
                "host", "localhost",
                "port", "6379",
                "password", "password",
                "db", "0"
        ));
    }

    @Test
    void testRedisConnectionFactory() {
        RedisConnectionFactory factory = redisTemplateConfig.redisConnectionFactory();

        assertNotNull(factory);
        assertTrue(factory instanceof LettuceConnectionFactory);

        LettuceConnectionFactory lettuceFactory = (LettuceConnectionFactory) factory;
        assertEquals("localhost", lettuceFactory.getHostName());
        assertEquals(6379, lettuceFactory.getPort());
        assertEquals("password", lettuceFactory.getPassword());
        assertEquals(0, lettuceFactory.getDatabase());
    }

    @Test
    void testRedisTemplate() {
        RedisTemplate<String, Object> template = redisTemplateConfig.redisTemplate(mockRedisConnectionFactory);

        assertNotNull(template);
        assertEquals(mockRedisConnectionFactory, template.getConnectionFactory());
        assertTrue(template.getKeySerializer() instanceof StringRedisSerializer);
        assertTrue(template.getValueSerializer() instanceof GenericJackson2JsonRedisSerializer);
        assertTrue(template.getHashKeySerializer() instanceof StringRedisSerializer);
        assertTrue(template.getHashValueSerializer() instanceof GenericJackson2JsonRedisSerializer);
    }
}