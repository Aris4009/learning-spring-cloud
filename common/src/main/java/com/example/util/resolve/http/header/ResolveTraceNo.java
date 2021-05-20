package com.example.util.resolve.http.header;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.example.exception.BusinessException;
import com.example.util.MyHttpHeaders;

public class ResolveTraceNo implements IResolveHttpHeader {

	private final HttpServletRequest request;

	public ResolveTraceNo(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public Map<String, String> resolve() throws BusinessException {
		int traceNo;
		if (request.getHeader(MyHttpHeaders.TRACE_NO_HEADER) != null) {
			traceNo = Integer.parseInt(request.getHeader(MyHttpHeaders.TRACE_NO_HEADER)) + 1;
		} else {
			traceNo = 0;
		}
		Map<String, String> map = new HashMap<>();
		map.put(MyHttpHeaders.TRACE_NO_HEADER, String.valueOf(traceNo));
		return map;
	}
}
