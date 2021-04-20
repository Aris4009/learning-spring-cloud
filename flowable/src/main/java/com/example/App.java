package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import com.ibeetl.starter.BeetlSqlSingleConfig;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, BeetlSqlSingleConfig.class})
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
