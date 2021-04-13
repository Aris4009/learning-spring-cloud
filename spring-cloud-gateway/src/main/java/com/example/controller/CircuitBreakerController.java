package com.example.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.response.entity.Response;

@RestController
@RequestMapping("/api/circuit/breaker")
public class CircuitBreakerController {

	@RequestMapping("/fallback")
	public Response<Void> fallBack() {
		return Response.fail("断路器回退接口");
	}
}
