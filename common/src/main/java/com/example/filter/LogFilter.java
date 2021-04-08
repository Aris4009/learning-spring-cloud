package com.example.filter;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.example.request.wrapper.RequestWrapper;

@Component
@WebFilter(urlPatterns = "/api/*")
public class LogFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		RequestWrapper requestWrapper = new RequestWrapper(httpServletRequest);
		chain.doFilter(requestWrapper, response);
	}
}
