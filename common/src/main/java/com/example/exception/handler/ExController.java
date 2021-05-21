package com.example.exception.handler;

import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import com.example.response.entity.Response;

/**
 * 处理错误页面
 */
@RestController
public class ExController implements ErrorController {

	@RequestMapping("/error")
	public Response<Void> ex(WebRequest request, HttpServletResponse response) {
		ErrorAttributeOptions options = ErrorAttributeOptions.defaults();
		DefaultErrorAttributes defaultErrorAttributes = new DefaultErrorAttributes();
		Map<String, Object> rawMap = defaultErrorAttributes.getErrorAttributes(request, options);
		HttpStatus httpStatus = getStatus(request);
		response.setStatus(HttpStatus.OK.value());
		return Response.fail(httpStatus.value(), rawMap.get("error").toString().toLowerCase());
	}

	protected HttpStatus getStatus(WebRequest request) {
		Integer httpStatus = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE,
				RequestAttributes.SCOPE_REQUEST);
		if (httpStatus == null) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
		try {
			return HttpStatus.valueOf(httpStatus);
		} catch (Exception ex) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
	}

	@Override
	public String getErrorPath() {
		return null;
	}
}
