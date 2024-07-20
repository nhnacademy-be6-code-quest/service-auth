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
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class RedisTemplateConfigTest {

    @Mock
    private RedisConnectionFactory mockRedisConnectionFactory;

    private RedisTemplateConfig redisTemplateConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        redisTemplateConfig = new RedisTemplateConfig("localhost", 6379, "password", 0);
    }

    @Test
    void testRedisConnectionFactory() {
        ReflectionTestUtils.setField(redisTemplateConfig, "redisHost", "testhost");
        ReflectionTestUtils.setField(redisTemplateConfig, "redisPort", 1234);
        ReflectionTestUtils.setField(redisTemplateConfig, "redisPassword", "testpassword");
        ReflectionTestUtils.setField(redisTemplateConfig, "redisDb", 1);

        RedisConnectionFactory factory = redisTemplateConfig.redisConnectionFactory();

        assertNotNull(factory);
        assertTrue(factory instanceof LettuceConnectionFactory);

        LettuceConnectionFactory lettuceFactory = (LettuceConnectionFactory) factory;
        assertEquals("testhost", lettuceFactory.getHostName());
        assertEquals(1234, lettuceFactory.getPort());
        assertEquals("testpassword", lettuceFactory.getPassword());
        assertEquals(1, lettuceFactory.getDatabase());
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