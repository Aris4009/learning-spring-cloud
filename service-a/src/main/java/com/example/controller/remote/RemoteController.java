package com.example.controller.remote;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.User;
import com.example.remote.client.IUserServiceClient;
import com.example.response.entity.Response;

@RestController
@RequestMapping("/api/remote")
public class RemoteController {

	private IUserServiceClient userServiceClient;

	public RemoteController(IUserServiceClient userServiceClient) {
		this.userServiceClient = userServiceClient;
	}

	@PostMapping("/user/list")
	public Response<List<User>> list(@RequestBody User user) {
		return userServiceClient.list(user);
	}
}
