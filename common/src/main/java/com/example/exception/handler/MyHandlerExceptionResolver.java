package com.example.exception.handler;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.example.json.JSON;

/**
 * 全局异常处理
 */
public class MyHandlerExceptionResolver implements HandlerExceptionResolver {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		try {
			Map<String, Object> map = ExResponseEntity.map(ex);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
			response.setStatus(HttpStatus.OK.value());
			response.getWriter().write(JSON.toJSONString(map));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return new ModelAndView();
	}
}
