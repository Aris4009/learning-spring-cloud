package com.example.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.response.entity.Response;

import cn.hutool.json.JSONObject;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/nacos")
public class NacosConfigController {

	private final WebClient webClient;

	public NacosConfigController(@Qualifier("client") WebClient webClient) {
		this.webClient = webClient;
	}

	@PostMapping("/list")
	public Mono<Response> list() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.set("username", "admin");
		return this.webClient.post().uri("http://service-b/api/v1/user/list").bodyValue(jsonObject).retrieve()
				.bodyToMono(Response.class);
	}

	@PostMapping("/list2")
	public Mono<Response> list2() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.set("username", "admin");
		return this.webClient.post().uri("lb://service-b/api/v1/user/list").bodyValue(jsonObject).retrieve()
				.bodyToMono(Response.class);
	}
}
