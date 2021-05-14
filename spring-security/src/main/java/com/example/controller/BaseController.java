package com.example.controller;

import javax.servlet.http.HttpServletRequest;

public class BaseController {

	public static final String AUTHORIZATION_HEADER = "Authorization";

	public static final String HEADER_X_AUTH_TOKEN = "X-Auth-Token";

	private final HttpServletRequest request;

	public BaseController(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletRequest getRequest() {
		return request;
	}
}
