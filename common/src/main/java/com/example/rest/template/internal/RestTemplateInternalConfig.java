package com.example.rest.template.internal;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.example.json.JSON;

import okhttp3.OkHttpClient;

@Configuration
public class RestTemplateInternalConfig {

	private static final String CONTENT_TYPE = "Content-Type";

	private static final String CONTENT_TYPE_VALUE = "application/json";

	/**
	 * 直接访问内部接口
	 * 
	 * @param okHttpClient
	 * @return
	 */
	@Bean(name = "restTemplateInternal")
	public RestTemplate restTemplateInternal(@Autowired OkHttpClient okHttpClient) {
		return restTemplate(okHttpClient);
	}

	/**
	 * 使用nacos服务发现访问内部接口
	 * 
	 * @param okHttpClient
	 * @return
	 */
	@LoadBalanced
	@Bean(name = "restTemplateNacos")
	public RestTemplate restTemplateNacos(@Autowired OkHttpClient okHttpClient) {
		return restTemplate(okHttpClient);
	}

	private RestTemplate restTemplate(OkHttpClient okHttpClient) {
		GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
		converter.setGson(JSON.gson);
		converter.setDefaultCharset(StandardCharsets.UTF_8);
		return new RestTemplateBuilder()
				.requestFactory(
						() -> new BufferingClientHttpRequestFactory(new OkHttp3ClientHttpRequestFactory(okHttpClient)))
				.defaultHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE).messageConverters(converter).build();
	}
}
