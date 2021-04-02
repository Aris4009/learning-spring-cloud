package com.example.interceptor;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import com.example.exception.BusinessException;
import com.example.json.JSON;
import com.example.request.wrapper.RequestWrapper;
import com.example.request.wrapper.RequestWrapperFacade;
import com.example.request.wrapper.UnWrapHttpServletRequestWrapper;

import lombok.Data;

/**
 * 请求日志对象
 */
@Data
public class RequestLog implements Serializable {

	private static final long serialVersionUID = -1032433027159174788L;

	// 实例id
	private String serviceId;

	// 时间
	private long time;

	// 时间-字符串表示
	private String timeStr;

	// 请求id
	private String requestId;

	// 请求url
	private String url;

	// 请求方法
	private String method;

	// 请求参数
	private String params;

	// 请求上传文件参数
	private String multipartParams;

	// 请求controller类名
	private String controller;

	// 请求controller类方法名
	private String controllerMethod;

	// 异常
	private transient Exception exception;

	// 类型 0-before 1-after 2-error
	private int type;

	private String errorMsg;

	private transient HttpServletRequest httpServletRequest;

	private transient Object handler;

	private static final int CAPACITY = 1024;

	public RequestLog() {
	}

	public RequestLog(String serviceId, String requestId, String url, HttpMethod httpMethod,
			HttpServletRequest httpServletRequest, Object handler) throws BusinessException {
		this.serviceId = serviceId;
		this.httpServletRequest = UnWrapHttpServletRequestWrapper.unwrap(httpServletRequest);
		this.requestId = requestId;
		this.url = url;
		this.method = httpMethod.name();
		try {
			if (!(handler instanceof ResourceHttpRequestHandler)) {
				HandlerMethod handlerMethod = (HandlerMethod) handler;
				this.controller = handlerMethod.getBeanType().getName();
				this.controllerMethod = handlerMethod.getMethod().getName();
			}

			// 解析请求参数
			this.params = getParams(httpServletRequest, httpMethod);
			this.multipartParams = getMultipartFilesInfo(httpServletRequest, httpMethod);
		} catch (Exception e) {
			throw new BusinessException(e);
		}
	}

	/**
	 * 获取GET/POST方法请求参数
	 *
	 * @param httpServletRequest 原始请求，没有被HttpServletRequestWrapper包装
	 * @param method             HttpMethod
	 * @return 返回请求体
	 * @throws BusinessException 异常
	 */
	public static String getParams(HttpServletRequest httpServletRequest, HttpMethod method) throws BusinessException {
		try {
			StringBuilder builder = new StringBuilder(CAPACITY);
			if (method == HttpMethod.GET) {
				builder.append(JSON.toJSONString(httpServletRequest.getParameterMap()));
			} else if (method == HttpMethod.POST) {
				RequestWrapperFacade requestWrapperFacade = new RequestWrapperFacade(httpServletRequest);
				RequestWrapper requestWrapper = requestWrapperFacade.getRequestWrapper();
				builder.append(requestWrapper.getRequestBody());
			}
			return builder.toString();
		} catch (Exception e) {
			throw new BusinessException(e);
		}
	}

	/**
	 * 获取POST multipart/form-data中上传的文件信息
	 *
	 * @param httpServletRequest 原始请求，没有被HttpServletRequestWrapper包装
	 * @param method             HttpMethod
	 * @return 返回文件信息（文件名-文件大小的key-value对）
	 * @throws BusinessException 异常
	 */
	public static String getMultipartFilesInfo(HttpServletRequest httpServletRequest, HttpMethod method)
			throws BusinessException {
		try {
			if (method == HttpMethod.POST) {
				StringBuilder multipartBuilder = new StringBuilder(CAPACITY);
				String contentType = httpServletRequest.getContentType();
				RequestWrapperFacade requestWrapperFacade = new RequestWrapperFacade(httpServletRequest);
				RequestWrapper requestWrapper = requestWrapperFacade.getRequestWrapper();
				if (contentType != null && contentType.equalsIgnoreCase(MediaType.MULTIPART_FORM_DATA.getType())
						&& requestWrapper.getMultipartFileListBody() != null) {
					multipartBuilder.append(requestWrapper.getMultipartFileListBody());
				}
				return multipartBuilder.toString();
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new BusinessException(e);
		}
	}

	/**
	 * 设置异常
	 * 
	 * @param exception 异常信息
	 */
	public void setException(Exception exception) {
		if (exception == null) {
			return;
		}
		this.exception = new Exception(exception);
		try (StringWriter stringWriter = new StringWriter(); PrintWriter printWriter = new PrintWriter(stringWriter);) {
			this.exception.printStackTrace(printWriter);
			this.errorMsg = stringWriter.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置时间
	 */
	public void setTime() {
		LocalDateTime localDateTime = LocalDateTime.now();
		this.time = localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() / 1000;
		this.timeStr = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

	public void beforeType() {
		this.type = 0;
	}

	public void afterType() {
		this.type = 1;
	}

	public void errorType() {
		this.type = 2;
	}

	public String toString() {
		return JSON.toJSONString(this);
	}
}
