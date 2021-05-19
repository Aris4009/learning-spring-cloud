package com.example.remote.interceptor;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import com.example.constant.MyHttpHeader;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Component
public class MyRequestInterceptor implements RequestInterceptor {

	@Override
	@SuppressWarnings("unchecked")
	public void apply(RequestTemplate template) {
		Map<String, String> requestAttributes = (Map<String, String>) RequestContextHolder.currentRequestAttributes();
		template.header(MyHttpHeader.REQUEST_ID_HEADER, requestAttributes.get(MyHttpHeader.REQUEST_ID_HEADER));
		template.header(MyHttpHeader.TRACE_NO_HEADER, requestAttributes.get(MyHttpHeader.TRACE_NO_HEADER));
		template.header(MyHttpHeader.X_AUTH_TOKEN_HEADER, requestAttributes.get(MyHttpHeader.X_AUTH_TOKEN_HEADER));
		template.header(MyHttpHeader.AUTHORIZATION_HEADER, requestAttributes.get(MyHttpHeader.AUTHORIZATION_HEADER));
	}
}
