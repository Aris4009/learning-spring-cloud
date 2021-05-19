package com.example.response.entity;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class Response<T> {

	private T data;

	private String message;

	private int status;

	private String timestamp;

	public static <T> Response<T> ok(T data, String... msg) {
		String s = null;
		if (msg != null && msg.length > 0) {
			StringBuilder builder = new StringBuilder();
			for (String m : msg) {
				builder.append(m);
			}
			s = builder.toString();
		} else {
			s = "success";
		}
		return (Response<T>) Response.builder().data(data).message(s).status(HttpStatus.OK.value())
				.timestamp(LocalDateTime.now().toString()).build();
	}

	public static <T> Response<T> ok(String... msg) {
		return ok(null, msg);
	}

	public static <T> Response<T> fail(T data, int status, String... msg) {
		String s = null;
		if (msg != null && msg.length > 0) {
			StringBuilder builder = new StringBuilder();
			for (String m : msg) {
				builder.append(m);
			}
			s = builder.toString();
		} else {
			s = "fail";
		}
		return (Response<T>) Response.builder().data(data).message(s).status(status)
				.timestamp(LocalDateTime.now().toString()).build();
	}

	public static <T> Response<T> fail(int status, String... msg) {
		return fail(null, status, msg);
	}

	public static <T> Response<T> fail(String... msg) {
		return fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
	}
}
