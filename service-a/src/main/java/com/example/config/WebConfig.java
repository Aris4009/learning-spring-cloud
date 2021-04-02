package com.example.config;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.interceptor.IStoreLog;
import com.example.interceptor.LogHandlerInterceptor;
import com.example.json.JSON;

/**
 * web配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final RequestLogConfig requestLogConfig;

	private final String serviceId;

	public WebConfig(RequestLogConfig requestLogConfig, @Value("${spring.application.name}") String serviceId) {
		this.serviceId = serviceId;
		this.requestLogConfig = requestLogConfig;
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
		converter.setGson(JSON.gson);
		converter.setDefaultCharset(StandardCharsets.UTF_8);
		converters.add(0, converter);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		List<IStoreLog> list = new ArrayList<>();
		registry.addInterceptor(new LogHandlerInterceptor(this.serviceId, this.requestLogConfig, list));
	}
}
