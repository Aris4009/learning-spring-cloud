package com.example.controller.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.response.entity.Response;

@RestController
@RequestMapping("/api/v1/config")
@RefreshScope
public class ConfigController {

	@Value("${spring.datasource.url}")
	private String url;

	@GetMapping("/get")
	public Response<String> get() {
		return Response.ok(this.url);
	}
}
