package com.example.rest.template.internal;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.example.exception.InternalClientException;
import com.example.store.log.IStoreLog;
import com.example.store.log.StoreLogUtil;
import com.example.util.MyRequestContext;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Component("okHttpInterceptor")
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.INTERFACES)
public class OkHttpInterceptor implements Interceptor {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private List<IStoreLog> storeLogList;

	public void setStoreLogList(List<IStoreLog> storeLogList) {
		this.storeLogList = storeLogList;
	}

	@NotNull
	@Override
	public Response intercept(@NotNull Chain chain) throws IOException {
		Request request = chain.request();
		LocalDateTime localDateTime = LocalDateTime.now();
		long start = localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
		String url = request.url().toString();
		String method = request.method();
		int bodyLength = 0;
		if (!Objects.isNull(request.body())) {
			bodyLength = Long.valueOf(request.body().contentLength()).intValue();
		}
		int responseStatus = -1;
		String responseBody = null;
		RestTemplateLog restTemplateLog = RestTemplateLog.builder().url(url).method(method).bodyLength(bodyLength)
				.startTime(localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() / 1000)
				.startTimeStr(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).build();
		try {
			restTemplateLog.setRequestId(MyRequestContext.getRequestId());
			restTemplateLog.setTraceNo(MyRequestContext.getTraceNo());
			Response response = chain.proceed(request);
			Response copyResponse = new Response.Builder(response).build();
			responseStatus = response.code();
			ResponseBody peek = response.peekBody(1024 * 1024);
			responseBody = peek.string();
			return copyResponse;
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
