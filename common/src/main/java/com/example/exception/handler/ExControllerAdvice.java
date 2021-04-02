package com.example.exception.handler;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.example.config.RequestLogConfig;
import com.example.exception.BusinessException;
import com.example.exception.ErrorPathException;
import com.example.interceptor.IStoreLog;
import com.example.interceptor.LogHandlerInterceptor;
import com.example.interceptor.RequestLog;
import com.example.interceptor.StoreLogUtil;

/**
 * 统一错误处理controller
 */
@ControllerAdvice
public class ExControllerAdvice {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final RequestLogConfig requestLogConfig;

	private List<IStoreLog> storeLogList;

	public ExControllerAdvice(RequestLogConfig requestLogConfig) {
		this.requestLogConfig = requestLogConfig;
	}

	public void setStoreLogList(List<IStoreLog> storeLogList) {
		this.storeLogList = storeLogList;
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> ex(@RequestBody Exception ex, WebRequest request) {
		ServletWebRequest servletWebRequest = (ServletWebRequest) request;
		HttpServletRequest httpServletRequest = servletWebRequest.getRequest();
		Map<String, Object> map = ExResponseEntity.map(ex, request);
		RequestLog requestLog = (RequestLog) httpServletRequest
				.getAttribute(LogHandlerInterceptor.REQUEST_LOG_ATTRIBUTE);
		if (requestLog == null) {
			requestLog = new RequestLog();
		}
		if (requestLogConfig.isError() && requestLog.getException() != null) {
			log.error("{}", requestLog);
		} else {
			if (ex instanceof HttpMessageNotReadableException) {
				requestLog.setErrorMsg("unsupported params type");
			} else if (ex instanceof HttpRequestMethodNotSupportedException) {
				requestLog.setErrorMsg("unsupported method");
			} else if (ex instanceof BusinessException) {
				requestLog.setErrorMsg(ex.getMessage());
			} else if (ex instanceof ErrorPathException) {
				requestLog.setErrorMsg(ex.getMessage());
			} else {
				requestLog.setException(ex);
			}
		}
		requestLog.errorType();
		requestLog.setTime();
		StoreLogUtil.storeLog(storeLogList, requestLog);
		return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
