package com.example.rate.limiter;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Configuration;

import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

	public KeyResolver sessionKeyResolver() {
		return exchange -> {
			return Mono.just(exchange.getSession().map(session -> {
				session.getId();
			}));
		};
	}
}
