package com.example.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.exception.BusinessException;
import com.example.interceptor.RequestLog;

public final class MyRequestContext {

	private MyRequestContext() {

	}

	public static final String REQUEST_CONTEXT_KEY = RequestAttributes.REFERENCE_REQUEST;

	public static final int REQUEST_CONTEXT_SCOPE = RequestAttributes.SCOPE_REQUEST;

	public static final String REQUEST_CONTEXT_REQUEST_LOG_KEY = "requestLog";

	private static final Logger log = LoggerFactory.getLogger(MyRequestContext.class);

	public static void setBeforeRequestContext(HttpServletRequest request, HttpServletResponse response, Object handler,
			String serviceId) throws BusinessException {
		MyResolveHttpHeaders myResolveHttpHeaders = new MyResolveHttpHeaders(request, serviceId);
		RequestLog requestLog = RequestLog.before(myResolveHttpHeaders, request, handler);
		RequestAttributes requestAttributes = new ServletRequestAttributes(request);
		Map<String, Object> map = new HashMap<>(myResolveHttpHeaders.getHeaders());
		map.put(REQUEST_CONTEXT_REQUEST_LOG_KEY, requestLog);
		requestAttributes.setAttribute(REQUEST_CONTEXT_KEY, map, REQUEST_CONTEXT_SCOPE);
		RequestContextHolder.setRequestAttributes(requestAttributes);
		response.setHeader(MyHttpHeaders.SERVICE_ID_HEADER, myResolveHttpHeaders.getServiceId());
		response.setHeader(MyHttpHeaders.REQUEST_ID_HEADER, myResolveHttpHeaders.getRequestId());
		response.setHeader(MyHttpHeaders.TRACE_NO_HEADER, myResolveHttpHeaders.getTraceNo());
		response.setHeader(MyHttpHeaders.URL_HEADER, myResolveHttpHeaders.getUrl());
	}

	public static void setAfterRequestContext() throws BusinessException {
		RequestLog requestLog = getRequestLog();
		if (requestLog.getException() == null) {
			RequestLog.after(requestLog, RequestLog.AFTER);
		} else {
			RequestLog.after(requestLog, RequestLog.ERROR);
		}
	}

	public static RequestAttributes getRequestContext() {
		return RequestContextHolder.getRequestAttributes();
	}

	public static RequestLog getRequestLog() throws BusinessException {
		Map<String, Object> map = getRequestContextMap();
		return (RequestLog) map.get(REQUEST_CONTEXT_REQUEST_LOG_KEY);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getRequestContextMap() throws BusinessException {
		RequestAttributes requestAttributes = getRequestContext();
		Map<String, Object> map = (Map<String, Object>) requestAttributes.getAttribute(REQUEST_CONTEXT_KEY,
				REQUEST_CONTEXT_SCOPE);
		if (map == null) {
			throw new BusinessException("there is no attributes log in request context");
		}
		return map;
	}

	public static void setRequestContextException(Exception ex) {
		try {
			getRequestLog().setException(ex);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void clear() {
		RequestContextHolder.resetRequestAttributes();
	}
}
