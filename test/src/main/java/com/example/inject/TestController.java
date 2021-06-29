package com.example.inject;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class TestController {

	private SingletonBean singletonBean;

	public TestController(SingletonBean singletonBean) {
		this.singletonBean = singletonBean;
	}

	@RequestMapping("/test")
	public ResponseEntity<List<String>> test() {
		return ResponseEntity.ok(this.singletonBean.console());
	}
}
