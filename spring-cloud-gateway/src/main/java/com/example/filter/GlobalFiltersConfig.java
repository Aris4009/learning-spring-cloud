package com.example.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.DedupeResponseHeaderGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.SaveSessionGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.StripPrefixGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

/**
 * global: # filters: # strip.prefix: 1 # save.session: true #
 * dedupe.response.header: Access-Control-Allow-Credentials
 * Access-Control-Allow-Origin
 */
// @Configuration
public class GlobalFiltersConfig {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * 剥离前缀过滤器
	 * 
	 * @param parts
	 *            剥离前缀数
	 * @return 全局过滤器
	 */
	@Bean
	@Order(Integer.MIN_VALUE + 1)
	public GlobalFilter stripPrefixFilter(@Value("${spring.cloud.gateway.global.filters.strip.prefix}") int parts) {
		StripPrefixGatewayFilterFactory.Config config = new StripPrefixGatewayFilterFactory.Config();
		config.setParts(parts);
		return (exchange, chain) -> {
			log.info("剥离前缀过滤器");
			StripPrefixGatewayFilterFactory stripPrefixGatewayFilterFactory = new StripPrefixGatewayFilterFactory();
			return stripPrefixGatewayFilterFactory.apply(config).filter(exchange, chain);
		};
	}

	/**
	 * Session存储过滤器 在转发到下游调用之前，保存Session
	 * 
	 * @return Session存储过滤器
	 */
	@Bean
	@Order(Integer.MIN_VALUE + 2)
	@ConditionalOnProperty(name = "spring.cloud.gateway.global.filters.save.session", havingValue = "true")
	public GlobalFilter saveSessionFilter() {
		return (exchange, chain) -> {
			log.info("保存Session过滤器");
			return new SaveSessionGatewayFilterFactory().apply(null).filter(exchange, chain);
		};
	}

	@Bean
	@Order(Integer.MIN_VALUE + 3)
	public GlobalFilter dedupeResponseHeaderFilter(
			@Value("${spring.cloud.gateway.global.filters.dedupe.response.header}") String name) {
		DedupeResponseHeaderGatewayFilterFactory.Config config = new DedupeResponseHeaderGatewayFilterFactory.Config();
		config.setName(name);
		return (exchange, chain) -> {
			log.info("去除重复响应头过滤器");
			DedupeResponseHeaderGatewayFilterFactory dedupeResponseHeaderGatewayFilterFactory = new DedupeResponseHeaderGatewayFilterFactory();
			return dedupeResponseHeaderGatewayFilterFactory.apply(config).filter(exchange, chain);
		};
	}
}
