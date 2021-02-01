package com.online.judge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class OnlineJudgeApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineJudgeApplication.class, args);
	}

}
