package com.example.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.dao.PermissionDao;
import com.example.entity.Permission;
import com.example.entity.UserDetail;
import com.example.exception.BusinessException;
import com.example.utils.JwtUtils;
import com.google.gson.reflect.TypeToken;

import cn.hutool.core.util.StrUtil;

@Service
public class AuthService implements IAuthService {

	private final JwtUtils jwtUtils;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final PermissionDao permissionDao;

	public AuthService(JwtUtils jwtUtils, PermissionDao permissionDao) {
		this.jwtUtils = jwtUtils;
		this.permissionDao = permissionDao;
	}

	@Override
	public String refresh(String token) throws BusinessException {
		if (StrUtil.isBlankIfStr(token)) {
			throw new BusinessException(JwtUtils.INVALID_TOKEN);
		}
		try {
			return this.jwtUtils.refresh(token);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
			throw new BusinessException(JwtUtils.INVALID_TOKEN);
		}
	}

	@Override
	public void verify(String token) throws BusinessException {
		int code = this.jwtUtils.verify(token);
		if (code < 0) {
			throw new BusinessException(JwtUtils.INVALID_TOKEN);
		}
	}

	@Override
	public void verify(String token, String url) throws BusinessException {
		if (StrUtil.isBlankIfStr(token) || StrUtil.isBlankIfStr(url)) {
			throw new BusinessException(JwtUtils.UNANUTHORIZED);
		}
		UserDetail userDetail = this.jwtUtils.parse(token, new TypeToken<UserDetail>() {
		});
		Permission permission = new Permission();
		permission.setRoleId(userDetail.getRole().getId());
		permission.setUrl(url);
		int count = this.permissionDao.verifyPermissionByRoleId(permission);
		if (count != 1) {
			throw new BusinessException(JwtUtils.UNANUTHORIZED);
		}
	}
}
