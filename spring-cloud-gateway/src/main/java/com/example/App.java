package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;

@SpringBootApplication
@NacosPropertySource(groupId = "dev", dataId = "example", autoRefreshed = true)
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
