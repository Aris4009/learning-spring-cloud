package com.example.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.example.exception.BusinessException;
import com.example.util.resolve.http.header.*;

public class MyResolveHttpHeaders {

	private final String serviceId;

	private final String method;

	private final String requestId;

	private final String sessionId;

	private final String token;

	private final String traceNo;

	private final String url;

	private final Map<String, String> headers;

	public MyResolveHttpHeaders(HttpServletRequest request, String serviceId) throws BusinessException {
		this.serviceId = serviceId;
		this.headers = new HashMap<>();
		this.headers.putAll(new ResolveMethod(request).resolve());
		this.headers.putAll(new ResolveRequestId(request).resolve());
		this.headers.putAll(new ResolveServiceId(serviceId).resolve());
		this.headers.putAll(new ResolveSessionId(request).resolve());
		this.headers.putAll(new ResolveToken(request).resolve());
		this.headers.putAll(new ResolveTraceNo(request).resolve());
		this.headers.putAll(new ResolveUrl(request).resolve());
		this.method = this.headers.get(MyHttpHeaders.METHOD_HEADER);
		this.requestId = this.headers.get(MyHttpHeaders.REQUEST_ID_HEADER);
		this.sessionId = this.headers.get(MyHttpHeaders.X_AUTH_TOKEN_HEADER);
		this.token = this.headers.get(MyHttpHeaders.AUTHORIZATION_HEADER);
		this.traceNo = this.headers.get(MyHttpHeaders.TRACE_NO_HEADER);
		this.url = this.headers.get(MyHttpHeaders.URL_HEADER);
	}

	public String getServiceId() {
		return serviceId;
	}

	public String getMethod() {
		return method;
	}

	public String getRequestId() {
		return requestId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public String getToken() {
		return token;
	}

	public String getTraceNo() {
		return traceNo;
	}

	public String getUrl() {
		return url;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}
}
