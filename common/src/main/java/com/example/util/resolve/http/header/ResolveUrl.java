package com.example.util.resolve.http.header;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.example.exception.BusinessException;
import com.example.util.MyHttpHeaders;

public class ResolveUrl implements IResolveHttpHeader {

	private final HttpServletRequest request;

	public ResolveUrl(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public Map<String, String> resolve() throws BusinessException {
		Map<String, String> map = new HashMap<>();
		map.put(MyHttpHeaders.URL_HEADER, request.getRequestURI());
		return map;
	}
}
