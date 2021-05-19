package com.example.interceptor;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.config.RequestLogConfig;
import com.example.constant.MyHttpHeader;
import com.example.exception.BusinessException;
import com.example.exception.ErrorPathException;

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
		httpServletResponse.setHeader(MyHttpHeader.SERVICE_ID_HEADER, this.serviceId);

		String requestId = getRequestId(httpServletRequest);
		httpServletResponse.setHeader(MyHttpHeader.REQUEST_ID_HEADER, requestId);

		int traceNo = getTraceNo(httpServletRequest);
		httpServletResponse.setHeader(MyHttpHeader.TRACE_NO_HEADER, String.valueOf(traceNo));

		String url = httpServletRequest.getRequestURI();
		httpServletResponse.setHeader(MyHttpHeader.URL_HEADER, url);

		String method = null;
		if (httpMethod != null) {
			method = httpMethod.name();
		}
		try {
			RequestLog requestLog = RequestLog.before(this.serviceId, requestId, traceNo, url, httpMethod,
					httpServletRequest, handler);
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
			RequestLog requestLog = RequestLog.errorType(this.serviceId, requestId, traceNo, url, method, e);
			if (requestLogConfig.isError()) {
				log.error("{}", requestLog);
			} else {
				if (e instanceof HttpMessageNotReadableException) {
					requestLog.setErrorMsg("unsupported params type");
				} else if (e instanceof HttpRequestMethodNotSupportedException) {
					requestLog.setErrorMsg("unsupported method");
				} else if (e instanceof BusinessException) {
					requestLog.setErrorMsg(e.getMessage());
				} else if (e instanceof ErrorPathException) {
					requestLog.setErrorMsg(e.getMessage());
				} else {
					requestLog.setException(e);
				}
			}
			httpServletRequest.setAttribute(REQUEST_LOG_ATTRIBUTE, requestLog);
			StoreLogUtil.storeLog(storeLogList, requestLog);
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
		RequestLog beforeRequestLog = (RequestLog) httpServletRequest.getAttribute(REQUEST_LOG_ATTRIBUTE);
		RequestLog requestLog = RequestLog.afterType(beforeRequestLog);

		Exception exception = requestLog.getException();
		if (requestLogConfig.isError() && exception != null) {
			log.error("{}", requestLog);
		} else {
			log.info("{}", requestLog);
		}
		StoreLogUtil.storeLog(storeLogList, requestLog);
	}

	private String getRequestId(HttpServletRequest httpServletRequest) {
		String id = httpServletRequest.getHeader(MyHttpHeader.REQUEST_ID_HEADER);
		if (id == null) {
			return UUID.randomUUID().toString().replace("-", "");
		}
		return id;
	}

	private int getTraceNo(HttpServletRequest httpServletRequest) {
		int traceNo;
		if (httpServletRequest.getHeader(MyHttpHeader.TRACE_NO_HEADER) != null) {
			traceNo = Integer.parseInt(httpServletRequest.getHeader(MyHttpHeader.TRACE_NO_HEADER)) + 1;
		} else {
			traceNo = 0;
		}
		return traceNo;
	}
}
