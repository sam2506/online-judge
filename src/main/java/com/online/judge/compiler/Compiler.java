package com.online.judge.compiler;

import lombok.Setter;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "completed.testcases")
@Setter
public class Compiler {

    private String exchange;
    private String routingKey;

    @Autowired
    private RabbitTemplate template;

    @RabbitListener(queues = "pending_test_cases_queue")
    public void executeTest(CompileRequest compileRequest) {
        if((compileRequest.getTest().getX() + compileRequest.getTest().getY()) ==
                compileRequest.getOutput().getZ()) {
            System.out.println("passed");
            template.convertAndSend(exchange, routingKey, "test case passed");
        } else {
            System.out.println("failed");
        }
    }
}
