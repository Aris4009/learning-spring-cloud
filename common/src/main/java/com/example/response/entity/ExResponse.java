package com.example.response.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.ServletException;

import org.springframework.core.NestedRuntimeException;
import org.springframework.web.HttpMediaTypeNotSupportedException;

import com.example.exception.AuthenticationException;
import com.example.exception.BusinessException;
import com.example.exception.ErrorPathException;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class ExResponse {

	private String message;

	private int status;

	private String timestamp;

	private transient Exception ex;

	public static final int INTERNAL_STATUS = 500;

	public static final int INVALID_TOKEN_STATUS = 4001;

	public static ExResponse ex(Exception ex) {
		if (ex instanceof BusinessException || ex instanceof ErrorPathException) {
			return internalError(ex);
		}

		if (ex instanceof AuthenticationException) {
			return authenticationError(ex);
		}

		if (ex instanceof NestedRuntimeException || ex instanceof HttpMediaTypeNotSupportedException) {
			return internalError("unsupported params type");
		}

		if (ex instanceof ServletException) {
			return internalError("unsupported request");
		}

		return internalError("internal error");
	}

	private static ExResponse internalError(Exception e) {
		return ExResponse.builder().status(INTERNAL_STATUS).message(e.getMessage()).timestamp(getTimestamp()).ex(e)
				.build();
	}

	private static ExResponse internalError(String message) {
		BusinessException ex = new BusinessException(message);
		return ExResponse.builder().status(INTERNAL_STATUS).message(message).timestamp(getTimestamp()).ex(ex).build();
	}

	private static ExResponse authenticationError(Exception e) {
		return ExResponse.builder().status(INVALID_TOKEN_STATUS).message(e.getMessage()).timestamp(getTimestamp()).ex(e)
				.build();
	}

	private static String getTimestamp() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}
}
