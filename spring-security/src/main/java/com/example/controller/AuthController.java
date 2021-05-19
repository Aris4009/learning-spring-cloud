package com.example.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.example.constant.MyHttpHeader;
import com.example.exception.BusinessException;
import com.example.response.entity.Response;
import com.example.services.IAuthService;

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
	public Response<String> refresh(@RequestHeader(MyHttpHeader.AUTHORIZATION_HEADER) String token)
			throws BusinessException {
		return Response.ok(this.authService.refresh(token));
	}

	@RequestMapping("/verify/token")
	public Response<Void> verify(@RequestHeader(MyHttpHeader.AUTHORIZATION_HEADER) String token)
			throws BusinessException {
		this.authService.verify(token);
		return Response.ok();
	}

	@PostMapping("/authenticate")
	public Response<Void> verify(@RequestHeader(MyHttpHeader.AUTHORIZATION_HEADER) String token,
			@RequestBody Map<String, String> url) throws BusinessException {
		this.authService.verify(token, MapUtil.getStr(url, URL));
		return Response.ok();
	}
}
