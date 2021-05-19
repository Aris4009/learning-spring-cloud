package com.example.test;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.json.JSON;
import com.example.response.entity.Response;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final HttpSession httpSession;

	public TestController(HttpSession httpSession) {
		this.httpSession = httpSession;
	}

	@GetMapping("/slow")
	public Response<String> slowCall() throws InterruptedException {
		String slow = "slow";
		Thread.sleep(7000);
		return Response.ok(JSON.toJSONString(slow));
	}

	@GetMapping("/retry")
	public Response<Void> retry() throws IOException {
		throw new IOException("error");
	}

	@GetMapping("/rate/limiter")
	public Response<Object> rateLimiter() {
		return Response.ok(this.httpSession.getId());
	}
}
