package com.example;

import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {RedissonAutoConfiguration.class, RabbitAutoConfiguration.class,
		DataSourceAutoConfiguration.class})
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
