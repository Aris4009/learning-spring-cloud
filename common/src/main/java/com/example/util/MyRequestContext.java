package com.example.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.exception.BusinessException;
import com.example.interceptor.RequestLog;
import com.example.util.resolve.http.header.IResolveHttpHeader;

public final class MyRequestContext {

	public static final String REQUEST_CONTEXT_KEY = RequestAttributes.REFERENCE_REQUEST;

	public static final int REQUEST_CONTEXT_SCOPE = RequestAttributes.SCOPE_REQUEST;

	public static final String REQUEST_CONTEXT_REQUEST_LOG_KEY = "requestLog";

	public static void set(IResolveHttpHeader resolveHttpHeader)
			throws BusinessException {
		
		
		Map<String, Object> map = new HashMap<>();
		for (IResolveHttpHeader resolveHttpHeader : resolveHttpHeaderList) {
			Map<String, String> header = resolveHttpHeader.resolve(httpServletRequest);
			map.putAll(header);
		}
        RequestLog requestLog = 
		RequestAttributes requestAttributes = new ServletRequestAttributes(httpServletRequest);
	}

	public static void clear() {
		RequestContextHolder.resetRequestAttributes();
	}
}
