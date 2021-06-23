package com.example.rest.template.internal;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = false)
public class RestTemplateInternalConfig {

	@Bean(name = "restTemplateInternal")
	public RestTemplate restTemplateInternal(
			@Qualifier("restTemplateInternalRequestInterceptor") ClientHttpRequestInterceptor clientHttpRequestInterceptor) {
		RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
		restTemplateBuilder.setConnectTimeout(Duration.ofSeconds(5));
		restTemplateBuilder.setReadTimeout(Duration.ofSeconds(5));
		return restTemplateBuilder.build();
	}
}
