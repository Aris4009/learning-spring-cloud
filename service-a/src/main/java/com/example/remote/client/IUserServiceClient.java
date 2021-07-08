package com.example.remote.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.entity.User;
import com.example.remote.fallback.UserServiceClientFallback;
import com.example.response.entity.Response;

@FeignClient(name = "userServiceClient", url = "http://service-b.com", qualifiers = "userServiceClient", fallback = UserServiceClientFallback.class)
public interface IUserServiceClient {

	@PostMapping("/api/v1/user/list")
	Response<List<User>> list(User user);
}