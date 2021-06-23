package com.example.remote.url.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.exception.BusinessException;
import com.example.json.JSON;
import com.google.gson.reflect.TypeToken;

@Component
// @RefreshScope
public class RemoteUrlConfig {

	private final String type;

	private final String path;

	private static final Logger log = LoggerFactory.getLogger(RemoteUrlConfig.class);

	public RemoteUrlConfig(@Value("${remote-url.internal.type:#{null}}") String type,
			@Value("${remote-url.internal.path:#{null}}") String path) {
		this.type = type;
		this.path = path;
	}

	private static final Map<String, String> REMOTE_URL_MAP = new ConcurrentHashMap<>();

	private static final String FILE_TYPE = "file";

	private static final String SERVER_TYPE = "server";

	private static final RestTemplate restTemplate;

	static {
		RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
		restTemplateBuilder.setConnectTimeout(Duration.ofSeconds(5));
		restTemplateBuilder.setReadTimeout(Duration.ofSeconds(5));
		restTemplateBuilder.additionalInterceptors((request, body, execution) -> {
			log.info("get remote url config from {}", request.getURI());
			return execution.execute(request, body);
		});
		restTemplate = restTemplateBuilder.build();
	}

	/**
	 * 读取配置
	 * 
	 * @throws BusinessException
	 *             异常
	 */
	@PostConstruct
	public void config() throws BusinessException {
		if (type == null || path == null) {
			return;
		}
		if (type.equalsIgnoreCase(FILE_TYPE)) {
			try {
				ClassPathResource classPathResource = new ClassPathResource(path);
				File file = classPathResource.getFile();
				if (!file.exists()) {
					throw new BusinessException("can't find remote url config,type:" + type + ",path:" + path);
				}
				String str = Files.readString(Path.of(path));
				REMOTE_URL_MAP.clear();
				if (str != null) {
					REMOTE_URL_MAP.putAll(JSON.parse(str, new TypeToken<Map<String, String>>() {
					}));
				}
			} catch (IOException e) {
				throw new BusinessException(e);
			}
		} else if (type.equalsIgnoreCase(SERVER_TYPE)) {
			String body = null;
			try {
				ResponseEntity<String> responseEntity = restTemplate.getForEntity(path, String.class);
				body = responseEntity.getBody();
				if (responseEntity.getStatusCode().is2xxSuccessful()) {
					REMOTE_URL_MAP.clear();
					if (body != null) {
						REMOTE_URL_MAP.putAll(JSON.parse(body, new TypeToken<Map<String, String>>() {
						}));
					}
				} else {
					throw new BusinessException(
							"can't find remote url config,type:" + type + ",path:" + path + " response" + ":{}" + body);
				}
			} catch (Exception e) {
				throw new BusinessException(e);
			}
		} else {
			throw new BusinessException("unsupported remote url config type:" + type + ",it must be file or server");
		}
	}

	@PreDestroy
	public void destroy() {
		REMOTE_URL_MAP.clear();
	}

	/**
	 * 获取配置
	 * 
	 * @return 内部调用url配置
	 */
	public Map<String, String> getConfig() {
		return Collections.unmodifiableMap(REMOTE_URL_MAP);
	}
}
