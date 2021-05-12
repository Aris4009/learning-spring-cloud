package com.example.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.exception.BusinessException;
import com.example.utils.JwtUtils;

import cn.hutool.core.util.StrUtil;

@Service
public class AuthService implements IAuthService {

	private final JwtUtils jwtUtils;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public AuthService(JwtUtils jwtUtils) {
		this.jwtUtils = jwtUtils;
	}

	@Override
	public String refresh(String token) throws BusinessException {
		if (StrUtil.isBlankIfStr(token)) {
			throw new BusinessException("invalid token");
		}
		try {
			return this.jwtUtils.refresh(token);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
			throw new BusinessException("invalid token");
		}
	}

	@Override
	public void verify(String token) throws BusinessException {
		int code = this.jwtUtils.verify(token);
		if (code < 0) {
			throw new BusinessException("invalid token");
		}
	}
}
