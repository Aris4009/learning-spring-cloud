package com.example.inject;

import java.util.UUID;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.INTERFACES)
// @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode =
// ScopedProxyMode.INTERFACES)
public class RequestScopeBean implements IBean {

	private String key = UUID.randomUUID().toString();

	@Override
	public String test() {
		return this.key;
	}
}
