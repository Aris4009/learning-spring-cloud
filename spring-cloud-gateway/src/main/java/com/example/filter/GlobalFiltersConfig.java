package com.example.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.StripPrefixGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class GlobalFiltersConfig {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * 剥离前缀过滤器
	 * @param parts 剥离前缀数
	 * @return 全局过滤器
	 */
	@Bean
	@Order(Integer.MIN_VALUE + 1)
	public GlobalFilter stripPrefixFilter(@Value("${spring.cloud.gateway.global.filters.strip.prefix}") int parts) {
		StripPrefixGatewayFilterFactory.Config config = new StripPrefixGatewayFilterFactory.Config();
		config.setParts(parts);
		return (exchange, chain) -> {
			log.info("1");
			StripPrefixGatewayFilterFactory stripPrefixGatewayFilterFactory = new StripPrefixGatewayFilterFactory();
			return stripPrefixGatewayFilterFactory.apply(config).filter(exchange, chain);
		};
	}
}
