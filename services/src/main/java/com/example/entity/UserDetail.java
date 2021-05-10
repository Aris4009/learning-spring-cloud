package com.example.entity;

import com.example.common.JSON;

public class UserDetail {

	private final User user;

	private final Role role;

	private final String token;

	public UserDetail(User user, Role role, String token) {
		this.user = user;
		this.role = role;
		this.token = token;
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

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
