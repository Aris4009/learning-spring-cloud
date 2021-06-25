package com.example.remote.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RemoteConfiguration {

	@Bean(value = "remoteUrlConfig")
	public IRemoteConfig remoteUrlConfig(@Value("${remote-url.internal.type:#{null}}") String type,
			@Value("${remote-url.internal.path:#{null}}") String path,
			@Value("${remote-url.internal.serverAddr:#{null}}") String serverAddr,
			@Value("${remote-url.internal.namespace:#{null}}") String namespace,
			@Value("${remote-url.internal.dataId:#{null}}") String dataId,
			@Value("${remote-url.internal.group:#{null}}") String group) {
		RemoteConfigPro remoteConfigPro = RemoteConfigPro.builder().type(type).path(path).serverAddr(serverAddr)
				.namespace(namespace).dataId(dataId).group(group).build();
		return new RemoteConfigImpl(remoteConfigPro);
	}
}
