package com.example.remote.config;

import java.util.Map;

public interface IRemoteConfig {
	public void init() throws Exception;

	public void destroy();

	public Object get(String key);

	public Map<String, Object> getAll();
}
