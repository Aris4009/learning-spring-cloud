package com.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "spring.rabbitmq.enable", havingValue = "true")
public class RabbitMQConfig {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Bean(destroyMethod = "destroy")
	public CachingConnectionFactory connectionFactory(@Value("${spring.rabbitmq.host}") String host,
			@Value("${spring.rabbitmq.port}") int port) {
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
		cachingConnectionFactory.setHost(host);
		cachingConnectionFactory.setPort(port);
		log.info("connection rabbitmq single host {} port {} success", host, port);
		return cachingConnectionFactory;
	}

	@Bean
	public RabbitTemplate rabbitTemplate(@Autowired ConnectionFactory connectionFactory) {
		return new RabbitTemplate(connectionFactory);
	}

	@Bean
	public Queue myQueue() {
		return new Queue("myQueue");
	}
}
