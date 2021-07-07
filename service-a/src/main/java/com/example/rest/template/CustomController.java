package com.example.rest.template;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.entity.User;
import com.example.response.entity.Response;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomController {

	private final RestTemplate restTemplate;

	public CustomController(@Qualifier("restTemplateNacos") RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@PostMapping("/list")
	public Response<List<User>> list(@RequestBody User user) {
		HttpEntity<User> httpEntity = new HttpEntity<>(user);
		return this.restTemplate.postForObject("http://service-b/api/v1/user/list", httpEntity, Response.class);
	}
}
