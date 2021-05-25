package com.example.remote.interceptor;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.util.MyHttpHeaders;
import com.example.util.MyRequestContext;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Component
public class MyRequestInterceptor implements RequestInterceptor {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void apply(RequestTemplate template) {
		try {
			ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
					.getRequestAttributes();
			Map<String, Object> requestAttributes = MyRequestContext.getRequestContextMap();
			template.header(MyHttpHeaders.REQUEST_ID_HEADER,
					requestAttributes.get(MyHttpHeaders.REQUEST_ID_HEADER).toString());
			template.header(MyHttpHeaders.TRACE_NO_HEADER,
					requestAttributes.get(MyHttpHeaders.TRACE_NO_HEADER).toString());
			template.header(MyHttpHeaders.X_AUTH_TOKEN_HEADER,
					requestAttributes.get(MyHttpHeaders.X_AUTH_TOKEN_HEADER).toString());
			template.header(MyHttpHeaders.AUTHORIZATION_HEADER,
					requestAttributes.get(MyHttpHeaders.AUTHORIZATION_HEADER).toString());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
