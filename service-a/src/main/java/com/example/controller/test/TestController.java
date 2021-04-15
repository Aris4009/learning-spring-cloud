package com.example.controller.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import com.example.exception.BusinessException;
import com.example.json.JSON;
import com.example.response.entity.Response;

import cn.hutool.core.util.ObjectUtil;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private HttpServletRequest httpServletRequest;

	private HttpSession httpSession;

	public TestController(HttpServletRequest httpServletRequest, HttpSession httpSession) {
		this.httpServletRequest = httpServletRequest;
		this.httpSession = httpSession;
	}

	@PostMapping("/hello")
	public Response<Map<String, Object>> hello(@RequestBody Map<String, Object> params) {
		return Response.ok(params, httpServletRequest);
	}

	@PostMapping("/put")
	public Response<Object> put(@RequestBody Map<String, Object> params) throws BusinessException {
		String key = httpSession.getId();
		if (ObjectUtil.isEmpty(params)) {
			throw BusinessException.paramsMustBeNotEmptyOrNullError();
		}
		httpSession.setAttribute(key, JSON.toJSONString(params));
		return Response.ok(params, httpServletRequest);
	}

	@GetMapping("/get")
	public Response<String> get() {
		String key = httpSession.getId();
		log.info("sessionId:{}", key);
		return Response.ok(JSON.toJSONString(httpSession.getAttribute(key)), httpServletRequest);
	}

	@GetMapping("/slow")
	public Response<String> slowCall() throws InterruptedException {
		String slow = "slow";
		Thread.sleep(7000);
		return Response.ok(JSON.toJSONString(slow));
	}

	@GetMapping("/retry")
	public Response<Void> retry() throws IOException {
		throw new IOException("error");
	}

	@GetMapping("/xForward")
	public Response<Map<String, Object>> xForward(WebRequest webRequest) {
		Map<String, Object> map = new HashMap<>();
		webRequest.getHeaderNames().forEachRemaining(name -> {
			map.put(name, webRequest.getHeader(name));
		});
		return Response.ok(map, httpServletRequest);
	}
}
