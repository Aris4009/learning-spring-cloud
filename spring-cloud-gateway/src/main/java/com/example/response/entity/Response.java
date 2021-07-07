package com.example.response.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class Response<T> {

	private T data;

	private String message;

	private int status;

	private String timestamp;

	private transient HttpHeaders headers;

	@SuppressWarnings("unchecked")
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

	public static Response<Void> fail(int status, String... msg) {
		return fail(null, status, msg);
	}

	public static Response<Void> fail(String... msg) {
		return fail(null, 500, msg);
	}

	@SuppressWarnings("unchecked")
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
				.timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).build();
	}

	public static <T> Response<T> ok(String... msg) {
		return ok(null, msg);
	}
}
