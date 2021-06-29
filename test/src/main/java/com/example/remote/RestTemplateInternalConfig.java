package com.example.remote;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateInternalConfig {

	private static final String CONTENT_TYPE = "Content-Type";

	private static final String CONTENT_TYPE_VALUE = "application/json";

	@Bean(name = "restTemplateInternal")
	public RestTemplate restTemplateInternal(
			@Qualifier("restTemplateInternalRequestInterceptor") ClientHttpRequestInterceptor clientHttpRequestInterceptor) {
		return new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(5)).setReadTimeout(Duration.ofSeconds(5))
				.defaultHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE).additionalInterceptors(clientHttpRequestInterceptor)
				.build();
	}
}
