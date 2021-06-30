package com.example.rest.template.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.store.log.IStoreLog;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

@Configuration
public class FeignClientConfiguration {

	private static final int TIMEOUT = 5;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final List<IStoreLog> storeLogList = new ArrayList<>();

	@Bean
	public OkHttpClient okHttpClient(@Qualifier("feignClientRequestInterceptor") Interceptor interceptor) {
		return new OkHttpClient().newBuilder().connectTimeout(TIMEOUT, TimeUnit.SECONDS)
				.readTimeout(TIMEOUT, TimeUnit.SECONDS).addInterceptor(interceptor).build();
	}
}
