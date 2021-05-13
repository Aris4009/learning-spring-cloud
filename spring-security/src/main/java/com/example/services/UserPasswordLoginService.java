package com.example.services;

import org.springframework.stereotype.Service;

import com.example.dao.RoleDao;
import com.example.dao.UserDao;
import com.example.entity.User;
import com.example.entity.UserDetail;
import com.example.exception.BusinessException;
import com.example.utils.JwtUtils;

import cn.hutool.core.util.StrUtil;

@Service("userPasswordLoginService")
public class UserPasswordLoginService extends AbstractUserPasswordLoginService {

	public UserPasswordLoginService(UserDao userDao, RoleDao roleDao, JwtUtils jwtUtils) {
		super(userDao, roleDao, jwtUtils);
	}

	@Override
	protected void preLogin(User user) throws BusinessException {
		if (user == null) {
			throw BusinessException.paramsMustBeNotEmptyOrNullError("username", "password");
		}
		String username = user.getUsername();
		if (StrUtil.length(username) < 4 || StrUtil.length(username) > 10) {
			throw BusinessException.paramsError("username");
		}
		String password = user.getPassword();
		if (StrUtil.length(password) < 6) {
			throw BusinessException.paramsError("password");
		}
	}

	@Override
	protected UserDetail afterLogin(UserDetail userDetail) {
		String token = getJwtUtils().sign(userDetail);
		userDetail.setToken(token);
		return userDetail;
	}

	@Override
	protected void preLogout() {
	}

	@Override
	protected void afterLogout() {
	}
}
