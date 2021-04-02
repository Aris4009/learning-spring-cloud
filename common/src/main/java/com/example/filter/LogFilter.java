package com.example.filter;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.example.request.wrapper.RequestWrapper;
import com.example.request.wrapper.UnWrapHttpServletRequestWrapper;

@Component
@WebFilter(urlPatterns = "/api/*")
public class LogFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletRequest unwrapHttpServletRequest = UnWrapHttpServletRequestWrapper.unwrap(httpServletRequest);
		RequestWrapper requestWrapper = new RequestWrapper(unwrapHttpServletRequest);
		chain.doFilter(requestWrapper, response);
	}
}
