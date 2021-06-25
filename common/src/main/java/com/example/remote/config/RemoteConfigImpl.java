package com.example.remote.config;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.example.exception.BusinessException;
import com.example.json.JSON;
import com.google.gson.reflect.TypeToken;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.resource.ClassPathResource;

public class RemoteConfigImpl implements IRemoteConfig {

	private RemoteConfigPro remoteConfigPro;

	private final Map<String, Object> cache = new ConcurrentHashMap<>();

	public RemoteConfigImpl(RemoteConfigPro remoteConfigPro) {
		this.remoteConfigPro = remoteConfigPro;
	}

	private static final Logger log = LoggerFactory.getLogger(RemoteConfigImpl.class);

	private static final String FILE_TYPE = "file";

	private static final String SERVER_TYPE = "server";

	private static final String SERVER_ADDR = "serverAddr";

	private static final String NAMESPACE = "namespace";

	private static final long TIMEOUT = 5000L;

	@Override
	@PostConstruct
	public void init() throws BusinessException {
		if (remoteConfigPro == null) {
			return;
		}
		String type = remoteConfigPro.getType();
		String path = remoteConfigPro.getPath();
		String serverAddr = remoteConfigPro.getServerAddr();
		String namespace = remoteConfigPro.getNamespace();
		String dataId = remoteConfigPro.getDataId();
		String group = remoteConfigPro.getGroup();
		if (type.equalsIgnoreCase(FILE_TYPE)) {
			try {
				ClassPathResource classPathResource = new ClassPathResource(path);
				File file = classPathResource.getFile();
				if (!file.exists()) {
					throw new BusinessException("can't find remote config,type:" + type + ",path:" + path);
				}
				String str = classPathResource.readUtf8Str();
				this.cache.clear();
				if (str != null) {
					this.cache.putAll(JSON.parse(str, new TypeToken<Map<String, String>>() {
					}));
				}
			} catch (IORuntimeException e) {
				throw new BusinessException(e);
			}
		} else if (type.equalsIgnoreCase(SERVER_TYPE)) {
			try {
				Properties properties = new Properties();
				properties.setProperty(SERVER_ADDR, serverAddr);
				properties.setProperty(NAMESPACE, namespace);
				ConfigService configService = NacosFactory.createConfigService(properties);
				String content = configService.getConfig(dataId, group, TIMEOUT);
				this.cache.clear();
				if (content != null) {
					this.cache.putAll(JSON.parse(content, new TypeToken<Map<String, String>>() {
					}));
				}
				configService.addListener(dataId, group, new ListenerImpl(type, path, this.cache));
			} catch (Exception e) {
				throw new BusinessException(e);
			}
		} else {
			throw new BusinessException("unsupported remote config type:" + type + ",it must be file or server");
		}
		log.info("init remote config cache type:{}，path:{},cache size:{}", type, path, this.cache.size());
	}

	@Override
	@PreDestroy
	public void destroy() {
		if (remoteConfigPro == null) {
			return;
		}
		log.info("clear remote config cache type:{},path:{}", Optional.of(remoteConfigPro.getType()).orElse(null),
				Optional.of(remoteConfigPro.getPath()).orElse(null));
		this.cache.clear();
	}

	@Override
	public Map<String, Object> get() {
		return Collections.unmodifiableMap(this.cache);
	}

	static class ListenerImpl implements Listener {

		private final String type;

		private final String path;

		private final Map<String, Object> cache;

		public ListenerImpl(String type, String path, Map<String, Object> cache) {
			this.type = type;
			this.path = path;
			this.cache = cache;
		}

		@Override
		public Executor getExecutor() {
			return null;
		}

		@Override
		public void receiveConfigInfo(String configInfo) {
			log.info("refresh remote config cache type:{},path:{}", this.type, this.path);
			this.cache.clear();
			if (configInfo != null) {
				this.cache.putAll(JSON.parse(configInfo, new TypeToken<Map<String, String>>() {
				}));
			}
		}
	}
}
