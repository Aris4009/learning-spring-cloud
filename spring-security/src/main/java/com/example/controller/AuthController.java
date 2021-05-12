package com.example.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.exception.BusinessException;
import com.example.response.entity.Response;
import com.example.services.IAuthService;

@RestController
@RequestMapping("/api")
public class AuthController extends BaseController {

	private final IAuthService authService;

	public AuthController(IAuthService authService, HttpServletRequest request) {
		super(request);
		this.authService = authService;
	}

	@PostMapping("/refresh/token")
	public Response<String> refresh() throws BusinessException {
		return Response.ok(this.authService.refresh(token()), getRequest());
	}

	@PostMapping("/verify/token")
	public Response<Void> verify() throws BusinessException {
		this.authService.verify(token());
		return Response.ok(getRequest());
	}
}
