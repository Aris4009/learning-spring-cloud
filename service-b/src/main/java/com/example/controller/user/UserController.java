package com.example.controller.user;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.User;
import com.example.exception.BusinessException;
import com.example.response.entity.Response;
import com.example.services.UserService;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

	private UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/list")
	public Response<List<User>> list(@RequestBody User user) throws BusinessException {
		return Response.ok(userService.list(user));
	}
}
