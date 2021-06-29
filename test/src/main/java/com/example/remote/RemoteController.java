package com.example.remote;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/v1")
public class RemoteController {

	private RestTemplate restTemplate;

	public RemoteController(@Qualifier("restTemplateInternal") RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@RequestMapping("/hello")
	public ResponseEntity<String> hello() {
		return ResponseEntity.ok("hello");
	}

	@RequestMapping("/tt")
	public ResponseEntity<String> test() {
		return this.restTemplate.getForEntity("http://localhost:8080/api/v1/hello", String.class);
	}
}
