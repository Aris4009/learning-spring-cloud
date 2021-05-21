package com.example.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.response.entity.Response;

@RestController
@RequestMapping("/api/circuit/breaker")
public class CircuitBreakerController {

	@RequestMapping("/fallback")
	public Response<Void> fallBack(ServerHttpResponse response) {
		return Response.fail(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase().toLowerCase());
	}
}
