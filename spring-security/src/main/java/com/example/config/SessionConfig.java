package com.example.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

@EnableRedisHttpSession
@Import(SessionAutoConfiguration.class)
@ConditionalOnProperty(name = "spring.redis.enable", havingValue = "true")
public class SessionConfig {
	@Bean
	public HttpSessionIdResolver httpSessionIdResolver() {
		return HeaderHttpSessionIdResolver.xAuthToken();
	}
}
