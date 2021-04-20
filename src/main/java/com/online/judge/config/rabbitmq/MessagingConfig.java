package com.online.judge.config.rabbitmq;

import com.online.judge.compiler.CompilationResponse;
import com.online.judge.submission.models.SubmissionResponse;
import com.online.judge.test.TestCaseResponse;
import lombok.Setter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "submission")
@Setter
public class MessagingConfig {

    private String exchange;
    private String queueName;
    private String routingKey;

    @Bean
    public Queue queue() {
        return new Queue(queueName);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    @Bean
    public MessageConverter converter() {
        Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put(
                "com.docker.sandbox.testcase.TestCaseResponse", TestCaseResponse.class);
        idClassMapping.put(
                "com.docker.sandbox.compiler.CompilationResponse", CompilationResponse.class);
        idClassMapping.put(
                "com.docker.sandbox.submission.SubmissionResponse", SubmissionResponse.class);
        classMapper.setIdClassMapping(idClassMapping);
        messageConverter.setClassMapper(classMapper);
        return messageConverter;
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}

