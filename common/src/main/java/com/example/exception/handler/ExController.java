package com.example.exception.handler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 处理错误页面
 */
@Controller
public class ExController extends AbstractErrorController {

	public ExController(ErrorAttributes errorAttributes) {
		super(errorAttributes);
	}

	@RequestMapping("/error")
	public ResponseEntity<Map<String, Object>> ex(HttpServletRequest request) {
		ErrorAttributeOptions options = ErrorAttributeOptions.defaults();
		Map<String, Object> rawMap = getErrorAttributes(request, options);
		HttpStatus httpStatus = getStatus(request);
		Map<String, Object> map = new HashMap<>();
		map.put("message", rawMap.get("error").toString().toLowerCase());
		map.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		map.put("status", rawMap.get("status"));
		return new ResponseEntity<>(map, httpStatus);
	}

	@Override
	public String getErrorPath() {
		return null;
	}
}
