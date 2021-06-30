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
import com.example.remote.client.IUserServiceClient;
import com.example.remote.config.IRemoteConfig;
import com.example.response.entity.Response;

@RestController
@RequestMapping("/api/v1")
public class RemoteController {

	private IUserServiceClient userServiceClient;

	private RestTemplate restTemplateInternal;

	private RestTemplate restTemplateNacos;

	private IRemoteConfig remoteConfig;

	public RemoteController(IUserServiceClient userServiceClient,
			@Qualifier("restTemplateInternal") RestTemplate restTemplateInternal,
			@Qualifier("restTemplateNacos") RestTemplate restTemplateNacos,
			@Qualifier("remoteUrlConfig") IRemoteConfig remoteConfig) {
		this.userServiceClient = userServiceClient;
		this.restTemplateInternal = restTemplateInternal;
		this.restTemplateNacos = restTemplateNacos;
		this.remoteConfig = remoteConfig;
	}

	/**
	 * 直接使用FeignClient访问内部接口
	 * 
	 * @param user
	 * @param request
	 * @return
	 */
	@PostMapping("/user/list")
	public Response<List<User>> list(@RequestBody User user, HttpServletRequest request) {
		return userServiceClient.list(user);
	}

	/**
	 * 直接使用RestTemplate访问内部接口，内部接口由nginx代理
	 * 
	 * @param user
	 * @return
	 */
	@PostMapping("/rest/template/user/list")
	public Response<List<User>> restTemplate(@RequestBody User user) {
		return restTemplateInternal.postForObject(remoteConfig.get("service-b/api/v1/user/list").toString(), user,
				Response.class);
	}

	/**
	 * 使用nacos服务发现访问内部接口
	 * 
	 * @param user
	 * @return
	 */
	@PostMapping("/rest/template/nacos/user/list")
	public Response<List<User>> restTemplateNacos(@RequestBody User user) {
		return restTemplateNacos.postForObject(remoteConfig.get("nacos/service-b/api/v1/user/list").toString(), user,
				Response.class);
	}
}
