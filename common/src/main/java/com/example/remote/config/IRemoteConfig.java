package com.example.remote.config;

import java.util.Map;

import com.example.exception.BusinessException;

public interface IRemoteConfig {
	public void init() throws BusinessException;

	public void destroy();

	public Object get(String key);

	public Map<String, Object> getAll();
}
