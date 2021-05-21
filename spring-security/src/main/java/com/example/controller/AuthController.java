package com.example.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.example.exception.AuthenticationException;
import com.example.response.entity.Response;
import com.example.services.IAuthService;
import com.example.util.MyHttpHeaders;

import cn.hutool.core.map.MapUtil;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

	private final IAuthService authService;

	private static final String URL = "url";

	public AuthController(IAuthService authService) {
		this.authService = authService;
	}

	@RequestMapping("/refresh/token")
	public Response<String> refresh(@RequestHeader(MyHttpHeaders.AUTHORIZATION_HEADER) String token)
			throws AuthenticationException {
		return Response.ok(this.authService.refresh(token));
	}

	@RequestMapping("/verify/token")
	public Response<Void> verify(@RequestHeader(MyHttpHeaders.AUTHORIZATION_HEADER) String token)
			throws AuthenticationException {
		this.authService.verify(token);
		return Response.ok();
	}

	@PostMapping("/authenticate")
	public Response<Void> verify(@RequestHeader(MyHttpHeaders.AUTHORIZATION_HEADER) String token,
			@RequestBody Map<String, String> url) throws AuthenticationException {
		this.authService.verify(token, MapUtil.getStr(url, URL));
		return Response.ok();
	}
}
