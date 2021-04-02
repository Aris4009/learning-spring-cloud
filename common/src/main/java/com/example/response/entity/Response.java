package com.example.response.entity;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.NativeWebRequest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class Response<T> {

	private T data;

	private String path;

	private String message;

	private int status;

	private String timestamp;

	private Object param;

	/**
	 * 返回通用响应对象
	 * 
	 * @param data 响应体
	 * @param <T>  泛型参数
	 * @return 响应体
	 */
	public static <T> Response<T> ok(T data) {
		String path = null;
		String[] msg = null;
		return ok(data, path, msg);
	}

	/**
	 * 返回通用响应对象
	 * 
	 * @param data    响应体
	 * @param request 请求体
	 * @param <T>     泛型参数
	 * @return 响应体
	 */
	public static <T> Response<T> ok(T data, HttpServletRequest request) {
		return ok(data, request, null);
	}

	/**
	 * 返回通用响应对象
	 *
	 * @param data    响应体
	 * @param request 请求体
	 * @param msg     响应消息
	 * @param <T>     泛型参数
	 * @return 响应体
	 */
	public static <T> Response<T> ok(T data, HttpServletRequest request, String[] msg) {
		return ok(data, request.getRequestURI(), msg);
	}

	public static Response<Void> ok(HttpServletRequest request, String[] msg) {
		return ok(null, request, msg);
	}

	public static Response<Void> ok(HttpServletRequest request) {
		return ok(null, request, null);
	}

	/**
	 * 返回通用响应对象
	 *
	 * @param data    响应体
	 * @param request 请求体
	 * @param <T>     泛型参数
	 * @return 响应体
	 */
	public static <T> Response<T> ok(T data, NativeWebRequest request) {
		return ok(data, request, null);
	}

	/**
	 * 返回通用响应对象
	 *
	 * @param data    响应体
	 * @param request 请求体
	 * @param msg     响应消息
	 * @param <T>     泛型参数
	 * @return 响应体
	 */
	public static <T> Response<T> ok(T data, NativeWebRequest request, String[] msg) {
		return ok(data, (HttpServletRequest) request.getNativeRequest(), msg);
	}

	public static Response<Void> ok(NativeWebRequest request, String[] msg) {
		return ok(null, request, msg);
	}

	public static Response<Void> ok(NativeWebRequest request) {
		return ok(null, request, null);
	}

	public static Response<Void> ok(String path, String[] msg) {
		return ok(null, path, msg);
	}

	/**
	 * 返回通用响应对象
	 * 
	 * @param data 响应体
	 * @param path 请求路径
	 * @param msg  响应消息
	 * @param <T>  泛型参数
	 * @return 响应体
	 */
	@SuppressWarnings("unchecked")
	public static <T> Response<T> ok(T data, String path, String[] msg) {
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
		return (Response<T>) Response.builder().data(data).path(path).message(s).status(200)
				.timestamp(LocalDateTime.now().toString()).build();
	}
}
