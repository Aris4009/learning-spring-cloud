package com.example.remote.config;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

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
import cn.hutool.http.HttpUtil;

public class RemoteConfigImpl implements IRemoteConfig {

	private final String type;

	private final String path;

	private final Map<String, Object> cache = new ConcurrentHashMap<>();

	public RemoteConfigImpl(String type, String path) {
		this.type = type;
		this.path = path;
	}

	private static final Logger log = LoggerFactory.getLogger(RemoteConfigImpl.class);

	private static final String FILE_TYPE = "file";

	private static final String SERVER_TYPE = "server";

	private static final String SERVER_ADDR = "serverAddr";

	private static final String DATA_ID = "dataId";

	private static final String GROUP = "group";

	private static final long timeout = 5000L;

	@Override
	public void init() throws BusinessException {
		if (type == null || path == null) {
			return;
		}
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
				Map<String, String> params = HttpUtil.decodeParamMap(path, StandardCharsets.UTF_8);
				String serverAddr = params.get(SERVER_ADDR);
				Properties properties = new Properties();
				properties.setProperty(SERVER_ADDR, serverAddr);
				String dataId = params.get(DATA_ID);
				String group = params.get(GROUP);
				ConfigService configService = NacosFactory.createConfigService(properties);
				String content = configService.getConfig(dataId, group, timeout);
				this.cache.clear();
				if (content != null) {
					this.cache.putAll(JSON.parse(content, new TypeToken<Map<String, String>>() {
					}));
				}
				configService.addListener(dataId, group, new ListenerImpl(this.cache));
			} catch (Exception e) {
				throw new BusinessException(e);
			}
		} else {
			throw new BusinessException("unsupported remote config type:" + type + ",it must be file or server");
		}
	}

	@Override
	public void destroy() {
		this.cache.clear();
	}

	@Override
	public Map<String, Object> get() {
		return Collections.unmodifiableMap(this.cache);
	}

	static class ListenerImpl implements Listener {

		private final Map<String, Object> cache;

		public ListenerImpl(Map<String, Object> cache) {
			this.cache = cache;
		}

		@Override
		public Executor getExecutor() {
			return null;
		}

		@Override
		public void receiveConfigInfo(String configInfo) {
			this.cache.clear();
			if (configInfo != null) {
				this.cache.putAll(JSON.parse(configInfo, new TypeToken<Map<String, String>>() {
				}));
			}
		}
	}
}
