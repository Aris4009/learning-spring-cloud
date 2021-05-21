package com.example.exception.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import com.example.exception.AuthenticationException;
import com.example.exception.BusinessException;
import com.example.exception.ErrorPathException;

public final class ExResponseEntity extends ResponseEntity<Map<String, Object>> {

	public ExResponseEntity(Map<String, Object> map) {
		super(map, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public static final String MESSAGE = "message";

	public static final String STATUS = "status";

	public static final int INTERNAL_STATUS = 500;

	public static final int INVALID_TOKEN_STATUS = 4001;

	public static Map<String, Object> map(Exception ex) {
		Map<String, Object> map = new HashMap<>();
		if (ex instanceof BusinessException || ex instanceof ErrorPathException) {
			return internalError(ex);
		}

		if (ex instanceof AuthenticationException) {
			return authenticationError(ex);
		}

		if (ex instanceof HttpMessageNotReadableException) {
			return internalError("unsupported params type");
		}

		if (ex instanceof HttpRequestMethodNotSupportedException) {
			return internalError("unsupported method");
		}

		return internalError("internal error");
	}

	private static Map<String, Object> internalError(Exception e) {
		Map<String, Object> map = new HashMap<>();
		map.put(STATUS, INTERNAL_STATUS);
		map.put(MESSAGE, e.getMessage());
		return map;
	}

	private static Map<String, Object> internalError(String... message) {
		Map<String, Object> map = new HashMap<>();
		map.put(STATUS, INTERNAL_STATUS);
		map.put(MESSAGE, message);
		return map;
	}

	private static Map<String, Object> authenticationError(Exception e) {
		Map<String, Object> map = new HashMap<>();
		map.put(STATUS, INVALID_TOKEN_STATUS);
		map.put(MESSAGE, e.getMessage());
		return map;
	}
}
