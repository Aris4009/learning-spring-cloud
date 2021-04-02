package com.example.controller.test;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.response.entity.Response;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private HttpServletRequest httpServletRequest;

	public TestController(HttpServletRequest httpServletRequest) {
		this.httpServletRequest = httpServletRequest;
	}

	@PostMapping("/hello")
	public Response<Map<String, Object>> hello(@RequestBody Map<String, Object> params) {
		return Response.ok(params, httpServletRequest);
	}
}
