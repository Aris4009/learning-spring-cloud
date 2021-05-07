package com.example.exception.handler;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import com.example.exception.ErrorPathException;

/**
 * 处理错误页面
 */
@Controller
public class SecurityExController extends ExController {

	@Override
	@RequestMapping("/security/error")
	public ResponseEntity<Map<String, Object>> ex(WebRequest request) {
		int httpCode =  request.getAttribute("javax.servlet.error.message", 0)
		ErrorPathException exception = new ErrorPathException(
				String.valueOf());
		Map<String, Object> map = ExResponseEntity.map(exception, request);
		return new ResponseEntity<>(map, HttpStatus
				.valueOf(Integer.parseInt(request.getAttribute("javax.servlet.error.status_code", 0).toString())));
	}

	@Override
	public String getErrorPath() {
		return null;
	}
}
