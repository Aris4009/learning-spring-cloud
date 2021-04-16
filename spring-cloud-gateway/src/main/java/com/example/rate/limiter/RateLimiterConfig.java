package com.example.rate.limiter;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebSession;

@Configuration
public class RateLimiterConfig {

	@Bean
	public KeyResolver sessionKeyResolver() {
		return exchange -> exchange.getSession().map(WebSession::getId);
	}
}
