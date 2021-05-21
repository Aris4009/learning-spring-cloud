package com.example.exception;

/**
 * 鉴权异常信息
 */
public class AuthenticationException extends Exception {

	private static final long serialVersionUID = 5232761315866661204L;

	public AuthenticationException(String message) {
		super(message);
	}

	public AuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthenticationException(Throwable cause) {
		super(cause);
	}
}
