package com.example.controller;

import org.springframework.web.bind.annotation.*;

import com.example.entity.User;
import com.example.entity.UserDetail;
import com.example.exception.BusinessException;
import com.example.response.entity.Response;
import com.example.services.ILoginService;
import com.example.util.MyResolveHttpHeaders;

@RestController
@RequestMapping("/api/v1")
public class LoginController {

	private final ILoginService loginService;

	public LoginController(ILoginService loginService) {
		this.loginService = loginService;
	}

	@PostMapping("/login")
	public Response<UserDetail> login(@RequestBody User user) throws BusinessException {
		return Response.ok(this.loginService.login(user));
	}

	@RequestMapping("/logout")
	public Response<Void> logout(@RequestHeader(MyResolveHttpHeaders.AUTHORIZATION_HEADER) String token)
			throws BusinessException {
		this.loginService.logout(token);
		return Response.ok();
	}
}
