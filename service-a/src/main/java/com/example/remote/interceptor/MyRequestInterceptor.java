package com.example.remote.interceptor;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import com.example.util.MyHttpHeaders;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Component
public class MyRequestInterceptor implements RequestInterceptor {

	@Override
	@SuppressWarnings("unchecked")
	public void apply(RequestTemplate template) {
		Map<String, String> requestAttributes = (Map<String, String>) RequestContextHolder.currentRequestAttributes();
		template.header(MyHttpHeaders.REQUEST_ID_HEADER, requestAttributes.get(MyHttpHeaders.REQUEST_ID_HEADER));
		template.header(MyHttpHeaders.TRACE_NO_HEADER, requestAttributes.get(MyHttpHeaders.TRACE_NO_HEADER));
		template.header(MyHttpHeaders.X_AUTH_TOKEN_HEADER, requestAttributes.get(MyHttpHeaders.X_AUTH_TOKEN_HEADER));
		template.header(MyHttpHeaders.AUTHORIZATION_HEADER, requestAttributes.get(MyHttpHeaders.AUTHORIZATION_HEADER));
	}
}
