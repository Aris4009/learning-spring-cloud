package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
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

	@Bean
	public RedisOperations<String, Object> sessionRedisOperations(
			@Autowired LettuceConnectionFactory lettuceConnectionFactory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(lettuceConnectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashKeySerializer(new Jackson2JsonRedisSerializer<>(Object.class));
		return redisTemplate;
	}
}
