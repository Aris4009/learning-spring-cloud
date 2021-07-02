package com.example.rest.template.internal;

import java.time.Duration;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Client;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

@Configuration
public class OkHttpClientConfiguration {

	private OkHttpClient okHttpClient;

	private static final int TIMEOUT = 5;

	private static final Duration DURATION_SECOND = Duration.ofSeconds(TIMEOUT);

	@Bean
	public OkHttpClient okHttpClient(@Qualifier("feignClientRequestInterceptor") Interceptor interceptor) {
		this.okHttpClient = new OkHttpClient().newBuilder().connectTimeout(DURATION_SECOND).readTimeout(DURATION_SECOND)
				.addInterceptor(interceptor).build();
		return this.okHttpClient;
	}

	@PreDestroy
	public void destory() {
		if (okHttpClient != null) {
			okHttpClient.dispatcher().executorService().shutdown();
			okHttpClient.connectionPool().evictAll();
		}
	}

	@Bean
	public Client feignClient(@Autowired OkHttpClient okHttpClient) {
		return new feign.okhttp.OkHttpClient(okHttpClient);
	}
}
