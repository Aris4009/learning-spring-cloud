package com.example.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;
import org.springframework.web.server.session.HeaderWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;

@EnableRedisWebSession
@ConditionalOnProperty(name = "spring.redis.enable", havingValue = "true")
public class SessionConfig {

	private static final String HEADER_X_AUTH_TOKEN = "X-Auth-Token";

	@Bean
	public WebSessionIdResolver httpSessionIdResolver() {
		HeaderWebSessionIdResolver headerWebSessionIdResolver = new HeaderWebSessionIdResolver();
		headerWebSessionIdResolver.setHeaderName(HEADER_X_AUTH_TOKEN);
		return headerWebSessionIdResolver;
	}
}
