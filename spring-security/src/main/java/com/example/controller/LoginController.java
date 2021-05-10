package com.example.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.User;

@RestController
@RequestMapping("/api")
public class LoginController {

	@PostMapping("/login")
	public String login(@RequestBody User user) {
		return null;
	}

	@PostMapping("/logout")
	public String logout() {
		return "2";
	}
}
