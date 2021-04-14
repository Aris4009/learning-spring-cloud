package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, SessionAutoConfiguration.class})
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
