package com.example.remote;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component("restTemplateInternalRequestInterceptor")
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.INTERFACES)
public class RestTemplateInternalRequestInterceptor implements ClientHttpRequestInterceptor {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		LocalDateTime localDateTime = LocalDateTime.now();
		long start = localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
		String url = request.getURI().toString();
		String method = request.getMethodValue();
		int bodyLength = Optional.ofNullable(body).orElse(new byte[0]).length;
		int responseStatus = -1;
		String responseBody = null;
		try {
			ClientHttpResponse clientHttpResponse = execution.execute(request, body);
			responseStatus = clientHttpResponse.getRawStatusCode();
			responseBody = new String(clientHttpResponse.getBody().readAllBytes());
			return clientHttpResponse;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			localDateTime = LocalDateTime.now();
			long end = localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
			log.info("[internal rest template]\n--------------------\n{}\n--------------------", this.hashCode());
		}
	}
}
