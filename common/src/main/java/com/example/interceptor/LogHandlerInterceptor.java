package com.example.interceptor;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.config.RequestLogConfig;
import com.example.exception.BusinessException;

public class LogHandlerInterceptor implements HandlerInterceptor {

	private final String serviceId;

	private final RequestLogConfig requestLogConfig;

	private final List<IStoreLog> storeLogList;

	public LogHandlerInterceptor(String serviceId, RequestLogConfig requestLogConfig) {
		this.serviceId = serviceId;
		this.requestLogConfig = requestLogConfig;
		this.storeLogList = null;
	}

	public LogHandlerInterceptor(String serviceId, RequestLogConfig requestLogConfig, List<IStoreLog> storeLogList) {
		this.serviceId = serviceId;
		this.requestLogConfig = requestLogConfig;
		this.storeLogList = storeLogList;
	}

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public static final String REQUEST_LOG_ATTRIBUTE = "requestLog";

	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object handler) throws Exception {
		HttpMethod httpMethod = HttpMethod.resolve(httpServletRequest.getMethod());
		if (httpMethod != HttpMethod.GET && httpMethod != HttpMethod.POST) {
			throw new BusinessException("unsupported " + httpMethod + " method");
		}

		String requestId = UUID.randomUUID().toString().replace("-", "");
		String url = httpServletRequest.getRequestURI();
		String method = null;
		if (httpMethod != null) {
			method = httpMethod.name();
		}
		try {
			RequestLog requestLog = new RequestLog(this.serviceId, requestId, url, httpMethod, httpServletRequest,
					handler);
			requestLog.beforeType();
			requestLog.setTime();

			httpServletRequest.setAttribute("request-id", requestId);
			httpServletRequest.setAttribute("url", url);
			if (url.equals("/error")) {
				return true;
			}

			if (requestLogConfig.isPre()) {
				log.info("{}", requestLog);
			}
			httpServletRequest.setAttribute(REQUEST_LOG_ATTRIBUTE, requestLog);
			StoreLogUtil.storeLog(storeLogList, requestLog);
			return true;
		} catch (Exception e) {
			// 封装预处理错误，由于此处发生异常，导致afterCompletion方法无法执行而采取的补救措施
			RequestLog requestLog = new RequestLog();
			requestLog.setRequestId(requestId);
			requestLog.setUrl(url);
			requestLog.setMethod(method);
			requestLog.setException(e);
			requestLog.errorType();
			requestLog.setTime();

			httpServletRequest.setAttribute("request-id", requestLog.getRequestId());
			httpServletRequest.setAttribute("url", requestLog.getUrl());
			httpServletRequest.setAttribute(REQUEST_LOG_ATTRIBUTE, requestLog);
			throw e;
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object handler, Exception ex) throws Exception {
		HttpMethod method = HttpMethod.resolve(httpServletRequest.getMethod());
		if (method != HttpMethod.GET && method != HttpMethod.POST) {
			return;
		}

		RequestLog requestLog = (RequestLog) httpServletRequest.getAttribute(REQUEST_LOG_ATTRIBUTE);
		requestLog.afterType();
		requestLog.setTime();

		Exception exception = requestLog.getException();
		if (requestLogConfig.isError() && exception != null) {
			log.error("{}", requestLog);
		} else {
			log.info("{}", requestLog);
		}
		StoreLogUtil.storeLog(storeLogList, requestLog);
	}
}
