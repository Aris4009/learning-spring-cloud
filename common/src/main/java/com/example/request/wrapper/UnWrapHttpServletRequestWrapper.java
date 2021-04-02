package com.example.request.wrapper;

import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * 由于HttpServletRequest可能被多个HttpServletWrapper包装，需要获取原始的HttpServletRequest
 */
public final class UnWrapHttpServletRequestWrapper {

	private UnWrapHttpServletRequestWrapper() {
	}

	/**
	 * 返回原始HttpServletRequest
	 * 
	 * @param httpServletRequest 包装HttpServletRequest
	 * @return 返回原始HttpServletRequest
	 */
	public static HttpServletRequest unwrap(HttpServletRequest httpServletRequest) {
		if (httpServletRequest instanceof ServletRequestWrapper) {
			HttpServletRequestWrapper httpServletRequestWrapper = (HttpServletRequestWrapper) httpServletRequest;
			return unwrap((HttpServletRequest) httpServletRequestWrapper.getRequest());
		} else {
			return httpServletRequest;
		}
	}
}
