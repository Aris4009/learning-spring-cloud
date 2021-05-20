package com.example.util.resolve.http.header;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpMethod;

import com.example.exception.BusinessException;
import com.example.util.MyHttpHeaders;

public class ResolveMethod implements IResolveHttpHeader {

	private final HttpServletRequest request;

	public ResolveMethod(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public Map<String, String> resolve() throws BusinessException {
		HttpMethod httpMethod = HttpMethod.resolve(request.getMethod());
		if (httpMethod == null) {
			throw new BusinessException("unsupported unknown method");
		}
		if (httpMethod != HttpMethod.GET && httpMethod != HttpMethod.POST) {
			throw new BusinessException("unsupported " + httpMethod + " method");
		}
		Map<String, String> map = new HashMap<>();
		map.put(MyHttpHeaders.METHOD_HEADER, httpMethod.name());
		return map;
	}
}
