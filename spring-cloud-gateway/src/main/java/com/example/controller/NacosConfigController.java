package com.example.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.response.entity.Response;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

@RestController
@RequestMapping("/api/v1/nacos")
public class NacosConfigController {

	private final WebClient webClient;

	public NacosConfigController(@Qualifier("client") WebClient webClient) {
		this.webClient = webClient;
	}

	@PostMapping("/list")
	public Response<Object> list() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.set("username", "admin");
		Object object = this.webClient.post().uri("http://service-b/ap1/v1/user/list").bodyValue(jsonObject).retrieve()
				.bodyToMono(Object.class).block();
		return Response.ok(JSONUtil.toJsonStr(object));
	}
}
