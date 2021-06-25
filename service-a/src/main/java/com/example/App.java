package com.example;

import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(exclude = {RedissonAutoConfiguration.class, RabbitAutoConfiguration.class,
		DataSourceAutoConfiguration.class, SessionAutoConfiguration.class})
@EnableFeignClients
@EnableDiscoveryClient
@EnableAspectJAutoProxy
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
