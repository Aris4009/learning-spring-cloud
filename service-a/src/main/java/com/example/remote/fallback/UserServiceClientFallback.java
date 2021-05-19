package com.example.remote.fallback;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.entity.User;
import com.example.remote.client.IUserServiceClient;
import com.example.response.entity.Response;

@Service
public class UserServiceClientFallback implements IUserServiceClient {

	@Override
	public Response<List<User>> list(User user) {
		return Response.fail(null, 500, "我是断路器");
	}
}
