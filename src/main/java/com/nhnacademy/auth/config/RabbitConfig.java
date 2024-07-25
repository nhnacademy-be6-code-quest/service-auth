package com.nhnacademy.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class RabbitConfig {
    private final Map<String, String> rabbitKey;

    @Value("${rabbit.login.exchange.name}")
    private String loginExchangeName;
    @Value("${rabbit.login.queue.name}")
    private String loginQueueName;
    @Value("${rabbit.login.routing.key}")
    private String loginRoutingKey;
    @Value("${rabbit.login.dlq.routing.key}")
    private String loginDlqRoutingKey;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitKey.get("host"), Integer.parseInt(rabbitKey.get("port")));
        connectionFactory.setUsername(rabbitKey.get("username"));
        connectionFactory.setPassword(rabbitKey.get("password"));
        return connectionFactory;
    }

    @Bean
    public DirectExchange loginExchange() {
        return new DirectExchange(loginExchangeName);
    }

    @Bean
    public Queue loginQueue() {
        return QueueBuilder.durable(loginQueueName)
                .withArgument("x-dead-letter-exchange", loginExchangeName)
                .withArgument("x-dead-letter-routing-key", loginDlqRoutingKey)
                .build();
    }

    @Bean
    public Binding loginBinding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(loginRoutingKey);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

}
