package com.example.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.example.exception.BusinessException;
import com.example.util.resolve.http.header.*;

public class MyResolveHttpHeaders implements IResolveHttpHeader {

	private final HttpServletRequest request;

	private final String serviceId;

	private String method;

	private String requestId;

	private String sessionId;

	private String token;

	private String traceNo;

	private String url;

	public MyResolveHttpHeaders(HttpServletRequest request, String serviceId) {
		this.request = request;
		this.serviceId = serviceId;
	}

	public HttpServletRequest getRequest() {
		return request;
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

	@Override
	public Map<String, String> resolve() throws BusinessException {
		Map<String, String> map = new HashMap<>();
		map.putAll(new ResolveMethod(request).resolve());
		map.putAll(new ResolveRequestId(request).resolve());
		map.putAll(new ResolveServiceId(serviceId).resolve());
		map.putAll(new ResolveSessionId(request).resolve());
		map.putAll(new ResolveToken(request).resolve());
		map.putAll(new ResolveTraceNo(request).resolve());
		map.putAll(new ResolveUrl(request).resolve());

		this.method = map.get(MyHttpHeaders.METHOD_HEADER);
		this.requestId = map.get(MyHttpHeaders.REQUEST_ID_HEADER);
		this.sessionId = map.get(MyHttpHeaders.X_AUTH_TOKEN_HEADER);
		this.token = map.get(MyHttpHeaders.AUTHORIZATION_HEADER);
		this.traceNo = map.get(MyHttpHeaders.TRACE_NO_HEADER);
		this.url = map.get(MyHttpHeaders.URL_HEADER);
		return map;
	}
}
