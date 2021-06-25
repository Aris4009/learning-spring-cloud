package com.example.controller.remote;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.entity.User;
import com.example.exception.BusinessException;
import com.example.remote.client.IUserServiceClient;
import com.example.remote.config.IRemoteConfig;
import com.example.response.entity.Response;

@RestController
@RequestMapping("/api/v1")
public class RemoteController {

	private IUserServiceClient userServiceClient;

	private RestTemplate restTemplate;

	private IRemoteConfig remoteConfig;

	public RemoteController(IUserServiceClient userServiceClient,
			@Qualifier("restTemplateInternal") RestTemplate restTemplate,
			@Qualifier("remoteUrlConfig") IRemoteConfig remoteConfig) {
		this.userServiceClient = userServiceClient;
		this.restTemplate = restTemplate;
		this.remoteConfig = remoteConfig;
	}

	@PostMapping("/user/list")
	public Response<List<User>> list(@RequestBody User user, HttpServletRequest request) throws BusinessException {
		return userServiceClient.list(user);
	}

	@PostMapping("/rest/template/user/list")
	public Response restTemplate(@RequestBody User user) {
		return restTemplate
				.postForEntity(remoteConfig.get("service-b/api/v1/user/list").toString(), user, Response.class)
				.getBody();
	}
}
