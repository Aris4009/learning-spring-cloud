package com.example.exception;

/**
 * ErrorControllerException异常
 */
public class ErrorPathException extends Exception {

	public ErrorPathException(String message) {
		super(message);
	}

	public ErrorPathException(String message, Throwable cause) {
		super(message, cause);
	}

	public ErrorPathException(Throwable cause) {
		super(cause);
	}

	protected ErrorPathException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
