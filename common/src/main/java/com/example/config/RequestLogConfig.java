package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 请求日志配置
 */
@Configuration
@Data
public class RequestLogConfig {

	@Value("${request.pre:true}")
	private boolean pre;

	@Value("${request.after:false}")
	private boolean error;

	@Value("${request.error:true}")
	private boolean after;
}
