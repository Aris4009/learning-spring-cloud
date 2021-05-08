package com.example.exception.handler;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class SecurityExController extends AbstractErrorController {

	public SecurityExController(ErrorAttributes errorAttributes) {
		super(errorAttributes);
	}

	final Logger log = LoggerFactory.getLogger(this.getClass());

	@RequestMapping("/security/error")
	public ResponseEntity<Map<String, Object>> ex(HttpServletRequest request) {
		ErrorAttributeOptions options = ErrorAttributeOptions.defaults();
		Map<String, Object> map = getErrorAttributes(request, options);
		HttpStatus httpStatus = getStatus(request);
		return new ResponseEntity<>(map, httpStatus);
	}

	@Override
	public String getErrorPath() {
		return null;
	}
}
