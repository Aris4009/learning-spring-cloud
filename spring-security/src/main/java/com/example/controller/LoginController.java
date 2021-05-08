package com.example.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LoginController {

	@PostMapping("/login")
	public String login() {
		return "1";
	}

	@PostMapping("/logout")
	public String logout() {
		return "2";
	}
}
