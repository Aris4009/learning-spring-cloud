package com.example.actuator;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.stereotype.Component;

@Component
@WebEndpoint(id = "customer.actuator.cache")
public class CustomerActuatorCache {

	private final Map<String, Object> cache = new HashMap<>();

	@ReadOperation
	public Map<String, Object> read() {
		return cache;
	}

	@WriteOperation
	public Map<String, Object> write(String key, Object value) {
		cache.put(key, value);
		return cache;
	}

	@DeleteOperation
	public Map<String, Object> delete(String key) {
		cache.remove(key);
		return cache;
	}
}
