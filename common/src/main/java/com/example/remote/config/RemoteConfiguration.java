package com.example.remote.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RemoteConfiguration {

	@Bean(value = "remoteUrlConfig", destroyMethod = "destroy")
	public IRemoteConfig remoteUrlConfig(@Value("${remote-url.internal.type:#{null}}") String type,
			@Value("${remote-url.internal.path:#{null}}") String path) {
		return new RemoteConfigImpl(type, path);
	}
}
