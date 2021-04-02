package com.example.request.wrapper;

import javax.servlet.http.HttpServletRequest;

/**
 * 提供可重用的request body
 */
public class RequestWrapperFacade {

	private final RequestWrapper requestWrapper;

	public RequestWrapperFacade(HttpServletRequest httpServletRequest) throws Exception {
		if (httpServletRequest instanceof RequestWrapper) {
			this.requestWrapper = (RequestWrapper) httpServletRequest;
			this.requestWrapper.getInputStream();
		} else {
			this.requestWrapper = new RequestWrapper(httpServletRequest);
		}
	}

	public RequestWrapper getRequestWrapper() {
		return requestWrapper;
	}
}
