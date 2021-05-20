package com.example.remote.interceptor;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import com.example.util.MyResolveHttpHeaders;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Component
public class MyRequestInterceptor implements RequestInterceptor {

	@Override
	@SuppressWarnings("unchecked")
	public void apply(RequestTemplate template) {
		Map<String, String> requestAttributes = (Map<String, String>) RequestContextHolder.currentRequestAttributes();
		template.header(MyResolveHttpHeaders.REQUEST_ID_HEADER,
				requestAttributes.get(MyResolveHttpHeaders.REQUEST_ID_HEADER));
		template.header(MyResolveHttpHeaders.TRACE_NO_HEADER,
				requestAttributes.get(MyResolveHttpHeaders.TRACE_NO_HEADER));
		template.header(MyResolveHttpHeaders.X_AUTH_TOKEN_HEADER,
				requestAttributes.get(MyResolveHttpHeaders.X_AUTH_TOKEN_HEADER));
		template.header(MyResolveHttpHeaders.AUTHORIZATION_HEADER,
				requestAttributes.get(MyResolveHttpHeaders.AUTHORIZATION_HEADER));
	}
}
