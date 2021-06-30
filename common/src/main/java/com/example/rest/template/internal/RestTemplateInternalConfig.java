package com.example.rest.template.internal;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.example.json.JSON;

@Configuration
public class RestTemplateInternalConfig {

	private static final String CONTENT_TYPE = "Content-Type";

	private static final String CONTENT_TYPE_VALUE = "application/json";

	private static final int TIMEOUT = 5;

	private static final Duration DURATION_SECOND = Duration.ofSeconds(TIMEOUT);

	/**
	 * 直接访问内部接口
	 * 
	 * @param clientHttpRequestInterceptor
	 * @return
	 */
	@Bean(name = "restTemplateInternal")
	public RestTemplate restTemplateInternal(
			@Qualifier("restTemplateInternalRequestInterceptor") ClientHttpRequestInterceptor clientHttpRequestInterceptor) {
		return restTemplate(clientHttpRequestInterceptor);
	}

	/**
	 * 使用nacos服务发现访问内部接口
	 * 
	 * @param clientHttpRequestInterceptor
	 * @return
	 */
	@LoadBalanced
	@Bean(name = "restTemplateNacos")
	public RestTemplate restTemplateNacos(
			@Qualifier("restTemplateInternalRequestInterceptor") ClientHttpRequestInterceptor clientHttpRequestInterceptor) {
		return restTemplate(clientHttpRequestInterceptor);
	}

	private RestTemplate restTemplate(ClientHttpRequestInterceptor clientHttpRequestInterceptor) {
		GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
		converter.setGson(JSON.gson);
		converter.setDefaultCharset(StandardCharsets.UTF_8);
		return new RestTemplateBuilder().setConnectTimeout(DURATION_SECOND).setReadTimeout(DURATION_SECOND)
				.requestFactory(() -> new BufferingClientHttpRequestFactory(new OkHttp3ClientHttpRequestFactory()))
				.defaultHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE).additionalInterceptors(clientHttpRequestInterceptor)
				.messageConverters(converter).build();
	}
}
