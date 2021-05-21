package com.example.interceptor;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.config.RequestLogConfig;
import com.example.exception.BusinessException;
import com.example.util.MyRequestContext;

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
		if ("/error".equals(httpServletRequest.getRequestURI())) {
			return true;
		}
		try {
			MyRequestContext.setBeforeRequestContext(httpServletRequest, httpServletResponse, handler, this.serviceId);
			RequestLog requestLog = MyRequestContext.getRequestLog();
			Map<String, Object> map = MyRequestContext.getRequestContextMap();
			if (requestLogConfig.isPre()) {
				log.info("{}", requestLog);
			}
			StoreLogUtil.storeLog(storeLogList, requestLog);
			return true;
		} catch (Exception e) {
			BusinessException businessException;
			if (e instanceof HttpMessageNotReadableException) {
				businessException = new BusinessException("unsupported params type");
			} else if (e instanceof HttpRequestMethodNotSupportedException) {
				businessException = new BusinessException("unsupported method");
			} else {
				businessException = new BusinessException(e);
			}
			RequestLog requestLog = RequestLog.modify(MyRequestContext.getRequestLog(), RequestLog.ERROR,
					businessException);
			if (requestLogConfig.isError()) {
				log.error("{}", requestLog);
			}
			StoreLogUtil.storeLog(storeLogList, requestLog);
			throw e;
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object handler, Exception ex) throws Exception {
		if ("/error".equals(httpServletRequest.getRequestURI())) {
			return;
		}
		RequestLog requestLog = RequestLog.modify(MyRequestContext.getRequestLog(), RequestLog.AFTER, null);
		Exception exception = requestLog.getException();
		if (requestLogConfig.isError() && exception != null) {
			log.error("{}", requestLog);
		} else {
			log.info("{}", requestLog);
		}
		StoreLogUtil.storeLog(storeLogList, requestLog);
		RequestContextHolder.resetRequestAttributes();
	}
}
