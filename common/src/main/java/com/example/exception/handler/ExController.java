package com.example.exception.handler;

import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.example.response.entity.ExResponse;
import com.example.util.MyRequestContext;

/**
 * 处理错误页面
 */
@RestController
public class ExController implements ErrorController {

	@RequestMapping("/error")
	public ResponseEntity<ExResponse> ex(WebRequest request, HttpServletResponse response) {
		DefaultErrorAttributes defaultErrorAttributes = new DefaultErrorAttributes();
		Exception ex = (Exception) defaultErrorAttributes.getError(request);
		MyRequestContext.setRequestContextException(ex);
		return ResponseEntity.ok(ExResponse.ex(ex));
	}

	@Override
	public String getErrorPath() {
		return null;
	}
}
