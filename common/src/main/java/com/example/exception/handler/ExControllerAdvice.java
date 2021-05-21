package com.example.exception.handler;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.response.entity.Response;

/**
 * 统一错误处理controller
 */
@ControllerAdvice
public class ExControllerAdvice {

	@ExceptionHandler(Exception.class)
	public Response<Void> ex(@RequestBody Exception ex, HttpServletResponse response) {
		Map<String, Object> map = ExResponseEntity.map(ex);
		int status = Integer.parseInt(map.get(ExResponseEntity.STATUS).toString());
		response.setStatus(HttpStatus.OK.value());
		return Response.fail(status, map.get(ExResponseEntity.MESSAGE).toString());
	}
}
