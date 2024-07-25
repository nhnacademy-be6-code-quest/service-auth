package com.nhnacademy.auth.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RabbitConfigTest {

    @Mock
    private ConnectionFactory mockConnectionFactory;

    private RabbitConfig rabbitConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rabbitConfig = new RabbitConfig(Map.of(
                "host", "localhost",
                "port", "5672",
                "username", "guest",
                "password", "guest"
        ));
        ReflectionTestUtils.setField(rabbitConfig, "loginExchangeName", "login.exchange");
        ReflectionTestUtils.setField(rabbitConfig, "loginQueueName", "login.queue");
        ReflectionTestUtils.setField(rabbitConfig, "loginRoutingKey", "login.key");
        ReflectionTestUtils.setField(rabbitConfig, "loginDlqRoutingKey", "login.dlq.key");
    }

    @Test
    void testConnectionFactory() {
        ConnectionFactory factory = rabbitConfig.connectionFactory();
        assertNotNull(factory);
        assertTrue(factory instanceof CachingConnectionFactory);
    }

    @Test
    void testLoginExchange() {
        DirectExchange exchange = rabbitConfig.loginExchange();
        assertNotNull(exchange);
        assertEquals("login.exchange", exchange.getName());
    }

    @Test
    void testLoginQueue() {
        Queue queue = rabbitConfig.loginQueue();
        assertNotNull(queue);
        assertEquals("login.queue", queue.getName());
        assertTrue(queue.isDurable());
    }

    @Test
    void testLoginBinding() {
        Queue queue = rabbitConfig.loginQueue();
        DirectExchange exchange = rabbitConfig.loginExchange();
        Binding binding = rabbitConfig.loginBinding(queue, exchange);

        assertNotNull(binding);
        assertEquals(Binding.DestinationType.QUEUE, binding.getDestinationType());
        assertEquals("login.key", binding.getRoutingKey());
    }

    @Test
    void testRabbitTemplate() {
        RabbitTemplate template = rabbitConfig.rabbitTemplate(mockConnectionFactory);
        assertNotNull(template);
        assertEquals(mockConnectionFactory, template.getConnectionFactory());
        assertTrue(template.getMessageConverter() instanceof Jackson2JsonMessageConverter);
    }
}