package com.example.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.User;
import com.example.entity.UserDetail;
import com.example.exception.BusinessException;
import com.example.response.entity.Response;
import com.example.services.ILoginService;

@RestController
@RequestMapping("/api")
public class LoginController extends BaseController {

	private final ILoginService loginService;

	public LoginController(ILoginService loginService, HttpServletRequest request) {
		super(request);
		this.loginService = loginService;
	}

	@PostMapping("/login")
	public Response<UserDetail> login(@RequestBody User user) throws BusinessException {
		return Response.ok(this.loginService.login(user), getRequest());
	}

	@PostMapping("/logout")
	public Response<Void> logout() throws BusinessException {
		this.loginService.logout(token());
		return Response.ok(getRequest());
	}
}
