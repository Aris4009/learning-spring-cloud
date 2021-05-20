package com.example.interceptor;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import com.example.exception.BusinessException;
import com.example.json.JSON;
import com.example.request.wrapper.RequestWrapper;
import com.example.request.wrapper.RequestWrapperFacade;
import com.example.util.MyHttpHeaders;

import lombok.Builder;
import lombok.Data;

/**
 * 请求日志对象
 */
@Data
@Builder
public class RequestLog implements Serializable {

	private static final long serialVersionUID = -1032433027159174788L;

	// 实例id
	private String serviceId;

	// 链路数值
	private int traceNo;

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

	private transient Object handler;

	private static final int CAPACITY = 1024;

	public RequestLog() {
	}

	public static RequestLog preHandle(Map<String, String> headers, HttpServletRequest httpServletRequest,
			Object handler) throws BusinessException {
		LocalDateTime localDateTime = LocalDateTime.now();
		RequestLog requestLog = new RequestLogBuilder().serviceId(headers.get(MyHttpHeaders.SERVICE_ID_HEADER))
				.requestId(headers.get(MyHttpHeaders.REQUEST_ID_HEADER))
				.traceNo(Integer.parseInt(headers.get(MyHttpHeaders.TRACE_NO_HEADER)))
				.url(headers.get(MyHttpHeaders.URL_HEADER)).method(headers.get(MyHttpHeaders.METHOD_HEADER))
				.time(localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() / 1000)
				.timeStr(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).build();
		try {
			if (!(handler instanceof ResourceHttpRequestHandler)) {
				HandlerMethod handlerMethod = (HandlerMethod) handler;
				requestLog.setController(handlerMethod.getBeanType().getName());
				requestLog.setControllerMethod(handlerMethod.getMethod().getName());
			}

			// 解析请求参数
			requestLog.setParams(
					getParams(httpServletRequest, HttpMethod.resolve(headers.get(MyHttpHeaders.METHOD_HEADER))));
			requestLog.setMultipartParams(getMultipartFilesInfo(httpServletRequest,
					HttpMethod.resolve(headers.get(MyHttpHeaders.METHOD_HEADER))));
		} catch (Exception e) {
			throw new BusinessException(e);
		}
	}

	public RequestLog(String serviceId, String requestId, int traceNo, String url, HttpMethod httpMethod,
			HttpServletRequest httpServletRequest, Object handler) throws BusinessException {
		this.serviceId = serviceId;
		this.requestId = requestId;
		this.traceNo = traceNo;
		this.url = url;
		this.method = httpMethod.name();
		LocalDateTime localDateTime = LocalDateTime.now();
		this.time = localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() / 1000;
		this.timeStr = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
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
	 * @param httpServletRequest
	 *            原始请求，没有被HttpServletRequestWrapper包装
	 * @param method
	 *            HttpMethod
	 * @return 返回请求体
	 * @throws BusinessException
	 *             异常
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
	 * @param httpServletRequest
	 *            原始请求，没有被HttpServletRequestWrapper包装
	 * @param method
	 *            HttpMethod
	 * @return 返回文件信息（文件名-文件大小的key-value对）
	 * @throws BusinessException
	 *             异常
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
	 * @param exception
	 *            异常信息
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

	public static RequestLog before(String serviceId, String requestId, int traceNo, String url, HttpMethod httpMethod,
			HttpServletRequest httpServletRequest, Object handler) throws BusinessException {
		RequestLog requestLog = new RequestLog(serviceId, requestId, traceNo, url, httpMethod, httpServletRequest,
				handler);
		requestLog.setType(0);
		return requestLog;
	}

	public static RequestLog afterType(RequestLog beforeRequestLog) {
		RequestLog requestLog = new RequestLog();
		requestLog.setServiceId(beforeRequestLog.getServiceId());
		requestLog.setRequestId(beforeRequestLog.getRequestId());
		requestLog.setTraceNo(beforeRequestLog.getTraceNo());
		requestLog.setUrl(beforeRequestLog.getUrl());
		LocalDateTime localDateTime = LocalDateTime.now();
		long time = localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() / 1000;
		String timeStr = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		requestLog.setTime(time);
		requestLog.setTimeStr(timeStr);
		requestLog.setMethod(beforeRequestLog.getMethod());
		requestLog.setParams(beforeRequestLog.getParams());
		requestLog.setMultipartParams(beforeRequestLog.getMultipartParams());
		requestLog.setController(beforeRequestLog.getController());
		requestLog.setControllerMethod(beforeRequestLog.getControllerMethod());
		requestLog.setException(beforeRequestLog.getException());
		requestLog.setType(1);
		requestLog.setErrorMsg(beforeRequestLog.getErrorMsg());
		return requestLog;
	}

	public static RequestLog errorType(String serviceId, String requestId, int traceNo, String url, String httpMethod,
			Exception e) {
		RequestLog requestLog = new RequestLog();
		requestLog.setServiceId(serviceId);
		requestLog.setRequestId(requestId);
		requestLog.setTraceNo(traceNo);
		requestLog.setUrl(url);
		requestLog.setMethod(httpMethod);
		requestLog.setException(e);
		requestLog.setType(2);
		LocalDateTime localDateTime = LocalDateTime.now();
		long time = localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() / 1000;
		String timeStr = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		requestLog.setTime(time);
		requestLog.setTimeStr(timeStr);
		return requestLog;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
