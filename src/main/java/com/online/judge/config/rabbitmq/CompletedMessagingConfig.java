package com.online.judge.config.rabbitmq;

import lombok.Setter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("completed.testcases")
@Setter
public class CompletedMessagingConfig {

    private String queueName;
    private String exchange;
    private String routingKey;

    @Bean
    public Queue queue1() {
        return new Queue(queueName);
    }

    @Bean
    public TopicExchange exchange1() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding binding1(Queue queue1, TopicExchange exchange) {
        return BindingBuilder.bind(queue1).to(exchange).with(routingKey);
    }

//    @Bean
//    public MessageConverter converter1() {
//        return new Jackson2JsonMessageConverter();
//    }
//
//    @Bean
//    public AmqpTemplate template1(ConnectionFactory connectionFactory1) {
//        final RabbitTemplate rabbitTemplate1 = new RabbitTemplate(connectionFactory1);
//        rabbitTemplate1.setMessageConverter(converter1());
//        return rabbitTemplate1;
//    }
}
