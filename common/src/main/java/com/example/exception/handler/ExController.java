package com.example.exception.handler;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.example.response.entity.Response;

/**
 * 处理错误页面
 */
@RestController
public class ExController implements ErrorController {

	@RequestMapping("/error")
	public Response<Void> ex(WebRequest request, HttpServletResponse response) {
		DefaultErrorAttributes defaultErrorAttributes = new DefaultErrorAttributes();
		Exception ex = (Exception) defaultErrorAttributes.getError(request);
		Map<String, Object> map = ExResponseEntity.map(ex);
		int status = Integer.parseInt(map.get(ExResponseEntity.STATUS).toString());
		response.setStatus(HttpStatus.OK.value());
		return Response.fail(status, map.get(ExResponseEntity.MESSAGE).toString());
	}

	@Override
	public String getErrorPath() {
		return null;
	}
}
