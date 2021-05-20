package com.example.util.resolve.http.header;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.example.exception.BusinessException;
import com.example.util.MyHttpHeaders;

public class ResolveToken implements IResolveHttpHeader {

	private final HttpServletRequest request;

	public ResolveToken(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public Map<String, String> resolve() throws BusinessException {
		String token = null;
		if (request.getHeader(MyHttpHeaders.AUTHORIZATION_HEADER) != null) {
			token = request.getHeader(MyHttpHeaders.AUTHORIZATION_HEADER);
		}
		Map<String, String> map = new HashMap<>();
		map.put(MyHttpHeaders.AUTHORIZATION_HEADER, token);
		return map;
	}
}
