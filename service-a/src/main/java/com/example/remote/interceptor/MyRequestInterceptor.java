package com.example.remote.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import com.example.constant.MyHttpHeader;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Component
public class MyRequestInterceptor implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate template) {
	    
        template.header(MyHttpHeader.REQUEST_ID_HEADER,RequestContextHolder.get)
	}
}
