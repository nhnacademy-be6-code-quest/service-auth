package com.nhnacademy.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitConfig {

    @Value("${rabbit.login.exchange.name}")
    private String loginExchangeName;
    @Value("${rabbit.login.queue.name}")
    private String loginQueueName;
    @Value("${rabbit.login.routing.key}")
    private String loginRoutingKey;

    @Bean
    DirectExchange loginExchange() {
        return new DirectExchange(loginExchangeName);
    }

    @Bean
    Queue loginQueue() {
        return new Queue(loginQueueName);
    }

    @Bean
    Binding loginBinding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(loginRoutingKey);
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

}
