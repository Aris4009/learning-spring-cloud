package com.example.config;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@ConditionalOnProperty(name = "spring.redis.enable", havingValue = "true")
public class RedisConfig {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Bean(name = "redisConfiguration")
	@ConditionalOnProperty(name = "spring.redis.type", havingValue = "0")
	public RedisStandaloneConfiguration redisStandaloneConfiguration(@Value("${spring.redis.host}") String host,
			@Value("${spring.redis.port}") int port, @Value("${spring.redis.database}") int dataBase) {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(host);
		redisStandaloneConfiguration.setPort(port);
		redisStandaloneConfiguration.setDatabase(dataBase);
		log.info("connection redis single server {}:{} database {} success", host, port, dataBase);
		return redisStandaloneConfiguration;
	}

	@Bean(name = "redisConfiguration")
	@ConditionalOnProperty(name = "spring.redis.type", havingValue = "1")
	public RedisClusterConfiguration redisConfiguration(@Value("${spring.redis.cluster.nodes}") String[] nodes) {
		RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
		for (String node : nodes) {
			String[] address = node.split(":");
			RedisNode redisNode = new RedisNode(address[0], Integer.parseInt(address[1]));
			redisClusterConfiguration.addClusterNode(redisNode);
		}
		log.info("connection redis cluster server {}", Arrays.toString(nodes));
		return redisClusterConfiguration;
	}

	@Bean
	public RedisConnectionFactory redisConnectionFactory(@Autowired RedisConfiguration redisConfiguration) {
		return new LettuceConnectionFactory(redisConfiguration);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(@Autowired RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
		return template;
	}

	@Bean
	public StringRedisTemplate stringRedisTemplate(@Autowired RedisConnectionFactory redisConnectionFactory) {
		StringRedisTemplate template = new StringRedisTemplate();
		template.setConnectionFactory(redisConnectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
		return template;
	}
}
