package com.example.response.entity;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.NativeWebRequest;

import com.example.constant.MyHttpHeader;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class Response<T> {

	private T data;

	private String serviceId;

	private String requestId;

	private String path;

	private String message;

	private int status;

	private String timestamp;

	public static <T> Response<T> ok(T data, String serviceId, String path, String requestId, String... msg) {
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
		return (Response<T>) Response.builder().data(data).serviceId(serviceId).path(path).requestId(requestId)
				.message(s).status(HttpStatus.OK.value()).timestamp(LocalDateTime.now().toString()).build();
	}

	public static <T> Response<T> ok(T data, HttpServletRequest request, String... msg) {
		String serviceId = request.getHeader(MyHttpHeader.SERVICE_ID_HEADER);
		String path = request.getRequestURI();
		String requestId = request.getHeader(MyHttpHeader.REQUEST_ID_HEADER);
		return ok(data, serviceId, path, requestId, msg);
	}

	public static <T> Response<T> ok(T data, NativeWebRequest request, String... msg) {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request.getNativeRequest();
		return ok(data, httpServletRequest, msg);
	}

	public static <T> Response<T> ok(HttpServletRequest request, String... msg) {
		return ok(null, request, msg);
	}

	public static <T> Response<T> ok(NativeWebRequest request, String... msg) {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request.getNativeRequest();
		return ok(httpServletRequest, msg);
	}

	public static <T> Response<T> fail(T data, String serviceId, String path, String requestId, int status,
			String... msg) {
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

	public static <T> Response<T> fail(T data, HttpServletRequest request, int status, String... msg) {
		String serviceId = request.getHeader(MyHttpHeader.SERVICE_ID_HEADER);
		String path = request.getRequestURI();
		String requestId = request.getHeader(MyHttpHeader.REQUEST_ID_HEADER);
		return fail(data, serviceId, path, requestId, status, msg);
	}

	public static <T> Response<T> fail(T data, HttpServletRequest request, String... msg) {
		return fail(data, request, HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
	}

	public static <T> Response<T> fail(HttpServletRequest request, String... msg) {
		return fail(null, request, msg);
	}

	public static <T> Response<T> fail(HttpServletRequest request, int status, String... msg) {
		return fail(null, request, status, msg);
	}

	public static <T> Response<T> fail(T data, NativeWebRequest request, int status, String... msg) {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request.getNativeRequest();
		return fail(data, httpServletRequest, status, msg);
	}

	public static <T> Response<T> fail(T data, NativeWebRequest request, String... msg) {
		return fail(data, request, HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
	}

	public static <T> Response<T> fail(NativeWebRequest request, String... msg) {
		return fail(null, request, msg);
	}

	public static <T> Response<T> fail(NativeWebRequest request, int status, String... msg) {
		return fail(null, request, status, msg);
	}
}
