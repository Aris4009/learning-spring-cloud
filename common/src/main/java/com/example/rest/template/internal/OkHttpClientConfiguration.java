package com.example.rest.template.internal;

import java.time.Duration;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

@Configuration
@Order(value = Ordered.LOWEST_PRECEDENCE)
public class OkHttpClientConfiguration {

	private OkHttpClient okHttpClient;

	private static final int TIMEOUT = 5;

	private static final Duration DURATION_SECOND = Duration.ofSeconds(TIMEOUT);

	@Bean
	public OkHttpClient okHttpClient(@Qualifier("okHttpInterceptor") Interceptor interceptor) {
		this.okHttpClient = new OkHttpClient().newBuilder().connectTimeout(DURATION_SECOND).readTimeout(DURATION_SECOND)
				.callTimeout(DURATION_SECOND).writeTimeout(DURATION_SECOND).addInterceptor(interceptor).build();
		return this.okHttpClient;
	}

	@PreDestroy
	public void destroy() {
		if (okHttpClient != null) {
			okHttpClient.dispatcher().executorService().shutdown();
			okHttpClient.connectionPool().evictAll();
		}
	}
}
