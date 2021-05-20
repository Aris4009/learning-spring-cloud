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
import com.example.util.MyResolveHttpHeaders;

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

	private RequestLog() {
	}

	public static final int BEFORE = 0;

	public static final int AFTER = 1;

	public static final int ERROR = 2;

	public static RequestLog before(MyResolveHttpHeaders myResolveHttpHeaders, HttpServletRequest httpServletRequest,
			Object handler) throws BusinessException {
		LocalDateTime localDateTime = LocalDateTime.now();
		RequestLog requestLog = new RequestLogBuilder().serviceId(myResolveHttpHeaders.getServiceId())
				.requestId(myResolveHttpHeaders.getRequestId())
				.traceNo(Integer.parseInt(myResolveHttpHeaders.getTraceNo())).url(myResolveHttpHeaders.getUrl())
				.method(myResolveHttpHeaders.getMethod())
				.time(localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() / 1000)
				.timeStr(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).type(BEFORE).build();
		try {
			if (!(handler instanceof ResourceHttpRequestHandler)) {
				HandlerMethod handlerMethod = (HandlerMethod) handler;
				requestLog.setController(handlerMethod.getBeanType().getName());
				requestLog.setControllerMethod(handlerMethod.getMethod().getName());
			}

			// 解析请求参数
			requestLog.setParams(getParams(httpServletRequest, HttpMethod.resolve(myResolveHttpHeaders.getMethod())));
			requestLog.setMultipartParams(
					getMultipartFilesInfo(httpServletRequest, HttpMethod.resolve(myResolveHttpHeaders.getMethod())));
		} catch (Exception e) {
			throw new BusinessException(e);
		}
		return requestLog;
	}

	public static RequestLog modify(RequestLog beforeRequestLog, int type, Exception e) {
		LocalDateTime localDateTime = LocalDateTime.now();
		beforeRequestLog.setTime(localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() / 1000);
		beforeRequestLog.setTimeStr(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		beforeRequestLog.setType(type);
		if (e != null) {
			beforeRequestLog.setException(e);
		}
		return beforeRequestLog;
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

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
