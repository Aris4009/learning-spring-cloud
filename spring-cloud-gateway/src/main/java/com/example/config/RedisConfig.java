package com.example.config;

import java.util.Arrays;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@ConditionalOnProperty(name = "spring.redis.enable", havingValue = "true")
public class RedisConfig {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Bean(destroyMethod = "shutdown", name = "redissonClient")
	@ConditionalOnProperty(name = "spring.redis.type", havingValue = "0")
	public RedissonClient redissonClient(@Value("${spring.redis.schema}") String schema,
			@Value("${spring.redis.host}") String host, @Value("${spring.redis.port}") int port,
			@Value("${spring.redis.database}") int dataBase) {
		Config config = new Config();
		String address = schema + "://" + host + ":" + port;
		config.useSingleServer().setAddress(address).setDatabase(dataBase);
		log.info("connection redis single server {} database {} success", address, dataBase);
		return Redisson.create(config);
	}

	@Bean(destroyMethod = "shutdown", name = "redissonClient")
	@ConditionalOnProperty(name = "spring.redis.type", havingValue = "1")
	public RedissonClient redissonClusterClient(@Value("${spring.redis.schema}") String schema,
			@Value("${spring.redis.cluster.nodes}") String[] nodes) {
		Config config = new Config();
		ClusterServersConfig clusterServersConfig = config.useClusterServers();
		for (String node : nodes) {
			clusterServersConfig.addNodeAddress(schema + "://" + node);
		}
		log.info("connection redis cluster server {}", Arrays.toString(nodes));
		return Redisson.create(config);
	}

	@Bean
	public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory(@Autowired RedissonClient redissonClient) {
		return new RedissonConnectionFactory(redissonClient);
	}

	@Bean
	public ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate(
			@Autowired ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(
				Object.class);
		RedisSerializationContext<Object, Object> serializationContext = RedisSerializationContext
				.newSerializationContext().key(jackson2JsonRedisSerializer).value(jackson2JsonRedisSerializer)
				.hashKey(jackson2JsonRedisSerializer).hashValue(jackson2JsonRedisSerializer).build();
		return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, serializationContext);
	}

	@Bean
	public ReactiveStringRedisTemplate reactiveStringRedisTemplate(
			@Autowired ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
		StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
		RedisSerializationContext<String, String> serializationContext = RedisSerializationContext
				.fromSerializer(stringRedisSerializer);
		return new ReactiveStringRedisTemplate(reactiveRedisConnectionFactory, serializationContext);
	}

	@Bean
	public RedissonReactiveClient redissonReactive(@Autowired RedissonClient redissonClient) {
		return redissonClient.reactive();
	}
}
