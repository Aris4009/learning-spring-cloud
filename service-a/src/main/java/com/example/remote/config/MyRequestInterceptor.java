package com.example.remote.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.util.MyHttpHeaders;
import com.example.util.MyRequestContext;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * 服务间调用，传递header的拦截器
 */
public class MyRequestInterceptor implements RequestInterceptor {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void apply(RequestTemplate template) {
		try {
			template.header(MyHttpHeaders.REQUEST_ID_HEADER, MyRequestContext.getRequestId());
			template.header(MyHttpHeaders.TRACE_NO_HEADER, MyRequestContext.getTraceNoStr());
			template.header(MyHttpHeaders.X_AUTH_TOKEN_HEADER, MyRequestContext.getXAuthToken());
			template.header(MyHttpHeaders.AUTHORIZATION_HEADER, MyRequestContext.getAuthorization());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
