package com.example.controller.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.remote.config.IRemoteConfig;
import com.example.response.entity.Response;

@RestController
@RequestMapping("/api/v1/config")
@RefreshScope
public class ConfigController {

	@Value("${spring.datasource.url}")
	private String url;

	private final IRemoteConfig remoteUrlConfig;

	public ConfigController(@Qualifier("remoteUrlConfig") IRemoteConfig remoteUrlConfig) {
		this.remoteUrlConfig = remoteUrlConfig;
	}

	@GetMapping("/get")
	public Response<String> get() {
		return Response.ok(this.url);
	}

	@GetMapping("/get/remote/url")
	public Response<Map<String, Object>> getRemoteUrl() {
		return Response.ok(remoteUrlConfig.getAll());
	}
}
