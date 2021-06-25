package com.example.rest.template.internal;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.example.json.JSON;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = false)
public class RestTemplateInternalConfig {

	private static String CONTENT_TYPE = "Content-Type";

	private static String CONTENT_TYPE_VALUE = "application/json";

	@Bean(name = "restTemplateInternal")
	public RestTemplate restTemplateInternal(
			@Qualifier("restTemplateInternalRequestInterceptor") ClientHttpRequestInterceptor clientHttpRequestInterceptor) {
		RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
		restTemplateBuilder.setConnectTimeout(Duration.ofSeconds(5));
		restTemplateBuilder.setReadTimeout(Duration.ofSeconds(5));
		restTemplateBuilder.requestFactory(OkHttp3ClientHttpRequestFactory.class);
		restTemplateBuilder.defaultHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE);
		restTemplateBuilder.additionalInterceptors(clientHttpRequestInterceptor);
		GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
		converter.setGson(JSON.gson);
		converter.setDefaultCharset(StandardCharsets.UTF_8);
		restTemplateBuilder.messageConverters(converter);
		return restTemplateBuilder.build();
	}
}
