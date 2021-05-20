package com.example.util.resolve.http.header;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.example.exception.BusinessException;
import com.example.util.MyHttpHeaders;

public class ResolveSessionId implements IResolveHttpHeader {

	private final HttpServletRequest request;

	public ResolveSessionId(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public Map<String, String> resolve() throws BusinessException {
		String sessionId = null;
		if (request.getHeader(MyHttpHeaders.X_AUTH_TOKEN_HEADER) != null) {
			sessionId = request.getHeader(MyHttpHeaders.X_AUTH_TOKEN_HEADER);
		}
		Map<String, String> map = new HashMap<>();
		map.put(MyHttpHeaders.X_AUTH_TOKEN_HEADER, sessionId);
		return map;
	}
}
