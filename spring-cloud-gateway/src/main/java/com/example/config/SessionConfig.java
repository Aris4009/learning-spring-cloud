package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;

@Configuration
@EnableRedisWebSession
public class SessionConfig {

	@Bean
	public RedisStandaloneConfiguration redisStandaloneConfiguration(@Value("${spring.redis.host}") String host,
			@Value("${spring.redis.port}") int port) {
		return new RedisStandaloneConfiguration(host, port);
	}

	@Bean
	public LettuceConnectionFactory lettuceConnectionFactory(
			@Autowired RedisStandaloneConfiguration redisStandaloneConfiguration) {
		return new LettuceConnectionFactory(redisStandaloneConfiguration);
	}
}
