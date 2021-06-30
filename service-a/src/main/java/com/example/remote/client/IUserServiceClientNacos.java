package com.example.remote.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.entity.User;
import com.example.response.entity.Response;

@FeignClient(name = "${feign.remote-service-b.name}", qualifiers = "${feign.remote-service-b.qualifiers}")
public interface IUserServiceClientNacos {
	@PostMapping("${feign.remote-service-b.path}")
	Response<List<User>> list(User user);
}
