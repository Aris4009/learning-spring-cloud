package com.example.exception.handler;

import java.util.Map;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.exception.ErrorPathException;

/**
 * 处理错误页面
 */
@Controller
public class ExController implements ErrorController {

	@RequestMapping("/error")
	public ResponseEntity<Map<String, Object>> ex() {
		ErrorPathException exception = new ErrorPathException("error path");
		Map<String, Object> map = ExResponseEntity.map(exception);
		return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public String getErrorPath() {
		return null;
	}
}
