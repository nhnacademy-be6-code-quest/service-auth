package com.nhnacademy.auth.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class RabbitConfigTest {

    @Autowired
    private RabbitConfig rabbitConfig;

    @MockBean
    private ConnectionFactory connectionFactory;

    @Test
    void testLoginExchange() {
        DirectExchange exchange = rabbitConfig.loginExchange();
        assertNotNull(exchange);
        assertEquals("code-quest.client.login.exchange", exchange.getName());
    }

    @Test
    void testLoginQueue() {
        Queue queue = rabbitConfig.loginQueue();
        assertNotNull(queue);
        assertEquals("code-quest.client.login.queue", queue.getName());
    }

    @Test
    void testLoginBinding() {
        Queue queue = rabbitConfig.loginQueue();
        DirectExchange exchange = rabbitConfig.loginExchange();
        Binding binding = rabbitConfig.loginBinding(queue, exchange);

        assertNotNull(binding);
        assertEquals("code-quest.client.login.key", binding.getRoutingKey());
        assertEquals(queue.getName(), binding.getDestination());
        assertEquals(exchange.getName(), binding.getExchange());
    }

    @Test
    void testRabbitTemplate() {
        RabbitTemplate rabbitTemplate = rabbitConfig.rabbitTemplate(connectionFactory);
        assertNotNull(rabbitTemplate);
        assertTrue(rabbitTemplate.getMessageConverter() instanceof org.springframework.amqp.support.converter.Jackson2JsonMessageConverter);
    }
}