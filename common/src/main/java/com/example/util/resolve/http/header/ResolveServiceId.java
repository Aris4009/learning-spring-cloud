package com.example.util.resolve.http.header;

import java.util.HashMap;
import java.util.Map;

import com.example.exception.BusinessException;
import com.example.util.MyHttpHeaders;

public class ResolveServiceId implements IResolveHttpHeader {

	private final String serviceId;

	public ResolveServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	@Override
	public Map<String, String> resolve() throws BusinessException {
		Map<String, String> map = new HashMap<>();
		map.put(MyHttpHeaders.SERVICE_ID_HEADER, this.serviceId);
		return map;
	}
}
