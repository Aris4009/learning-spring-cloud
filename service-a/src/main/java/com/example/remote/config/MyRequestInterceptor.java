package com.example.remote.config;

import java.util.Map;
import java.util.Objects;

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
			Map<String, Object> requestAttributes = MyRequestContext.getRequestContextMap();
			template.header(MyHttpHeaders.REQUEST_ID_HEADER,
					Objects.toString(requestAttributes.get(MyHttpHeaders.REQUEST_ID_HEADER), null));
			template.header(MyHttpHeaders.TRACE_NO_HEADER,
					Objects.toString(requestAttributes.get(MyHttpHeaders.TRACE_NO_HEADER), null));
			template.header(MyHttpHeaders.X_AUTH_TOKEN_HEADER,
					Objects.toString(requestAttributes.get(MyHttpHeaders.X_AUTH_TOKEN_HEADER), null));
			template.header(MyHttpHeaders.AUTHORIZATION_HEADER,
					Objects.toString(requestAttributes.get(MyHttpHeaders.AUTHORIZATION_HEADER), null));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
