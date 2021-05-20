package com.example.util.resolve.http.header;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.example.exception.BusinessException;
import com.example.util.MyHttpHeaders;

public class ResolveRequestId implements IResolveHttpHeader {

	private final HttpServletRequest request;

	public ResolveRequestId(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public Map<String, String> resolve() throws BusinessException {
		String id = request.getHeader(MyHttpHeaders.REQUEST_ID_HEADER);
		if (id == null) {
			id = UUID.randomUUID().toString().replace("-", "");
		}
		Map<String, String> map = new HashMap<>();
		map.put(MyHttpHeaders.REQUEST_ID_HEADER, id);
		return map;
	}
}
