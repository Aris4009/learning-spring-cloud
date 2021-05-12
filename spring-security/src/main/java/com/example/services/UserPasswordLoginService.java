package com.example.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.dao.RoleDao;
import com.example.dao.UserDao;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.entity.UserDetail;
import com.example.exception.BusinessException;
import com.example.utils.JwtUtils;
import com.google.gson.reflect.TypeToken;

import cn.hutool.core.util.StrUtil;

@Service("userPasswordLoginService")
public class UserPasswordLoginService implements ILoginService {

	private final UserDao userDao;

	private final RoleDao roleDao;

	private final JwtUtils jwtUtils;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public UserPasswordLoginService(UserDao userDao, RoleDao roleDao, JwtUtils jwtUtils) {
		this.userDao = userDao;
		this.roleDao = roleDao;
		this.jwtUtils = jwtUtils;
	}

	@Override
	public UserDetail login(User user) throws BusinessException {
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
		String rawPassword = user.getPassword();
		user = this.userDao.selectByUsername(user);
		if (user == null) {
			throw new BusinessException("invalid username or password");
		}
		String encodingPassword = user.getPassword();
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		boolean flag = bCryptPasswordEncoder.matches(rawPassword, encodingPassword);
		if (!flag) {
			throw new BusinessException("invalid username or password");
		}

		Role role = new Role();
		role.setUserId(user.getId());
		role = this.roleDao.selectRoleByUserId(role);
		if (role == null) {
			throw new BusinessException("invalid role");
		}

		user.setPassword(null);
		UserDetail userDetail = new UserDetail(user, role);
		String token = this.jwtUtils.sign(userDetail);
		userDetail.setToken(token);
		return userDetail;
	}

	@Override
	public void logout() throws BusinessException {
		this.jwtUtils.removeSession();
	}

	@Override
	public String refreshToken(String token) throws BusinessException {
		if (StrUtil.isBlankIfStr(token)) {
			throw new BusinessException("invalid token");
		}
		try {
			UserDetail userDetail = this.jwtUtils.parse(token, new TypeToken<UserDetail>() {
			});
			return this.jwtUtils.refresh(token, userDetail);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new BusinessException("invalid token");
		}
	}
}