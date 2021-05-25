package com.example.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.config.RequestLogConfig;
import com.example.util.MyRequestContext;

public class LogHandlerInterceptor implements HandlerInterceptor {

	private final String serviceId;

	private final String errorPath;

	private final RequestLogConfig requestLogConfig;

	private final List<IStoreLog> storeLogList;

	public LogHandlerInterceptor(String serviceId, String errorPath, RequestLogConfig requestLogConfig,
			List<IStoreLog> storeLogList) {
		this.serviceId = serviceId;
		this.errorPath = errorPath;
		this.requestLogConfig = requestLogConfig;
		this.storeLogList = storeLogList;
	}

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object handler) throws Exception {
		if (this.errorPath.equals(httpServletRequest.getRequestURI())) {
			return true;
		}
		MyRequestContext.setBeforeRequestContext(httpServletRequest, httpServletResponse, handler, this.serviceId);
		RequestLog requestLog = MyRequestContext.getRequestLog();
		if (requestLogConfig.isPre()) {
			log.info("{}", requestLog.console());
		}
		StoreLogUtil.storeLog(storeLogList, requestLog);
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object handler, Exception ex) throws Exception {
		if (this.errorPath.equals(httpServletRequest.getRequestURI())) {
			return;
		}
		MyRequestContext.setAfterRequestContext();
		RequestLog requestLog = MyRequestContext.getRequestLog();
		Exception exception = requestLog.getException();
		if (requestLogConfig.isError() && exception != null) {
			log.error("{}", requestLog.console());
		} else {
			log.info("{}", requestLog.console());
		}
		StoreLogUtil.storeLog(storeLogList, requestLog);
		RequestContextHolder.resetRequestAttributes();
	}
}
