package com.example.exception;

import java.io.IOException;

public class InternalClientException extends IOException {

	private static final long serialVersionUID = -1890032218735519675L;

	public InternalClientException() {
	}

	public InternalClientException(String message) {
		super(message);
	}

	public InternalClientException(String message, Throwable cause) {
		super(message, cause);
	}

	public InternalClientException(Throwable cause) {
		super(cause);
	}

	public static InternalClientException error(Throwable cause) {
		return new InternalClientException(cause);
	}
}
