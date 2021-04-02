package com.example.exception;

import java.util.Arrays;

public class BusinessException extends Exception {

	public BusinessException(String message) {
		super(message);
	}

	public BusinessException(String message, Throwable cause) {
		super(message, cause);
	}

	public BusinessException(Throwable cause) {
		super(cause);
	}

	/**
	 * 服务器内部错误
	 * 
	 * @return 服务器内部错误
	 */
	public static BusinessException internalError() {
		return new BusinessException("internal error");
	}

	/**
	 * 参数错误
	 * 
	 * @return 参数错误
	 */
	public static BusinessException paramsError() {
		return new BusinessException("params error");
	}

	/**
	 * 参数错误
	 * 
	 * @param param 请求参数名
	 * @return 参数错误
	 */
	public static BusinessException paramsError(String param) {
		return new BusinessException(param + " params error");
	}

	/**
	 * 参数错误
	 *
	 * @param param  请求参数名
	 * @param errMsg 错误消息
	 * @return 参数错误
	 */
	public static BusinessException paramsError(String param, String errMsg) {
		return new BusinessException(param + " " + errMsg);
	}

	/**
	 * 参数不能为空
	 * 
	 * @param name 请求参数名
	 * @return 参数不能为空
	 */
	public static BusinessException paramsMustBeNotEmptyOrNullError(String... name) {
		return new BusinessException("param " + Arrays.toString(name) + " must be not empty or null");
	}
}
