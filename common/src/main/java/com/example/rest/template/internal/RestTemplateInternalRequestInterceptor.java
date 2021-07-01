package com.example.rest.template.internal;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

import com.example.exception.InternalClientException;
import com.example.store.log.IStoreLog;
import com.example.store.log.StoreLogUtil;
import com.example.util.MyHttpHeaders;
import com.example.util.MyRequestContext;

import cn.hutool.core.util.StrUtil;

@Component("restTemplateInternalRequestInterceptor")
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.INTERFACES)
public class RestTemplateInternalRequestInterceptor implements ClientHttpRequestInterceptor {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private List<IStoreLog> storeLogList;

	public void setStoreLogList(List<IStoreLog> storeLogList) {
		this.storeLogList = storeLogList;
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws InternalClientException {
		LocalDateTime localDateTime = LocalDateTime.now();
		long start = localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
		String url = request.getURI().toString();
		String method = request.getMethodValue();
		int bodyLength = Optional.ofNullable(body).orElse(new byte[0]).length;
		int responseStatus = -1;
		String responseBody = null;
		RestTemplateLog restTemplateLog = RestTemplateLog.builder().url(url).method(method).bodyLength(bodyLength)
				.startTime(localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() / 1000)
				.startTimeStr(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).build();
		try {
			if (StrUtil.isNotEmpty(MyRequestContext.getRequestId())) {
				request.getHeaders().add(MyHttpHeaders.REQUEST_ID_HEADER, MyRequestContext.getRequestId());
			}
			if (StrUtil.isNotEmpty(MyRequestContext.getTraceNoStr())) {
				request.getHeaders().add(MyHttpHeaders.TRACE_NO_HEADER, MyRequestContext.getTraceNoStr());
			}
			if (StrUtil.isNotEmpty(MyRequestContext.getXAuthToken())) {
				request.getHeaders().add(MyHttpHeaders.X_AUTH_TOKEN_HEADER, MyRequestContext.getXAuthToken());
			}
			if (StrUtil.isNotEmpty(MyRequestContext.getAuthorization())) {
				request.getHeaders().add(MyHttpHeaders.AUTHORIZATION_HEADER, MyRequestContext.getAuthorization());
			}
			restTemplateLog.setRequestId(MyRequestContext.getRequestId());
			restTemplateLog.setTraceNo(MyRequestContext.getTraceNo());
			ClientHttpResponse clientHttpResponse = execution.execute(request, body);
			responseStatus = clientHttpResponse.getRawStatusCode();
			responseBody = new String(clientHttpResponse.getBody().readAllBytes());
			return clientHttpResponse;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			restTemplateLog.setErrorMsg(e.getMessage());
			throw InternalClientException.error(e);
		} finally {
			localDateTime = LocalDateTime.now();
			long end = localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
			restTemplateLog.setEndTime(localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() / 1000);
			restTemplateLog.setEndTimeStr(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			restTemplateLog.setExecTime(end - start);
			restTemplateLog.setResponseStatus(responseStatus);
			restTemplateLog.setResponseBody(responseBody);
			log.info("[internal rest template]\n--------------------\n{}\n--------------------", restTemplateLog);
			StoreLogUtil.storeLog(storeLogList, restTemplateLog);
		}
	}
}
