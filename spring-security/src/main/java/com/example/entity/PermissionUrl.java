package com.example.entity;

public class PermissionUrl {

	private final String name;

	private final String url;

	public PermissionUrl(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}
}
