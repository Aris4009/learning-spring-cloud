package com.example.controller;

import javax.servlet.http.HttpServletRequest;

public class BaseController {

	private static final String AUTHORIZATION_HEADER = "Authorization";

	private final HttpServletRequest request;

	public BaseController(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public String token() {
		return this.request.getHeader(AUTHORIZATION_HEADER);
	}
}
