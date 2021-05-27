package com.example.response.entity;

import java.time.LocalDateTime;

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
}
