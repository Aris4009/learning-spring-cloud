package com.example.exception.handler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.example.exception.BusinessException;
import com.example.exception.ErrorPathException;

public class ExResponseEntity extends ResponseEntity<Map<String, Object>> {

	public ExResponseEntity(Map<String, Object> map) {
		super(map, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private static final String MESSAGE = "message";

	public static Map<String, Object> map(Exception ex, WebRequest request) {
		Map<String, Object> map = new HashMap<>();
		map.put("timestamp", LocalDateTime.now().toString());
		map.put("status", 500);
		if (ex instanceof HttpMessageNotReadableException) {
			map.put(MESSAGE, "unsupported params type");
		} else if (ex instanceof HttpRequestMethodNotSupportedException) {
			map.put(MESSAGE, "unsupported method");
		} else if (ex instanceof BusinessException) {
			map.put(MESSAGE, ex.getMessage());
		} else if (ex instanceof ErrorPathException) {
			map.put(MESSAGE, ex.getMessage());
		} else {
			map.put(MESSAGE, "internal error");
		}
		ServletWebRequest servletWebRequest = (ServletWebRequest) request;
		map.put("path", servletWebRequest.getRequest().getRequestURI());
		return map;
	}
}
