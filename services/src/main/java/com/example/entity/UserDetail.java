package com.example.entity;

import com.example.common.JSON;

public class UserDetail {

	private final User user;

	private final Role role;

	private String token;

	public UserDetail(User user, Role role) {
		this.user = user;
		this.role = role;
	}

	public User getUser() {
		return user;
	}

	public Role getRole() {
		return role;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
