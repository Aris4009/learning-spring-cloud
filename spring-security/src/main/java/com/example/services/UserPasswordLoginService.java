package com.example.services;

import java.security.Key;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.dao.RoleDao;
import com.example.dao.UserDao;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.exception.BusinessException;

import cn.hutool.core.util.StrUtil;

@Service
public class UserPasswordLoginService implements ILoginService {

	private UserDao userDao;

	private RoleDao roleDao;

	private Key key;

	public UserPasswordLoginService(UserDao userDao, RoleDao roleDao, Key key) {
		this.userDao = userDao;
		this.roleDao = roleDao;
		this.key = key;
	}

	@Override
	public UserDetails login(User user) throws BusinessException {
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
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		String encodePassword = bCryptPasswordEncoder.encode(password);
		user.setPassword(encodePassword);
		user = this.userDao.single(user);
		if (user == null) {
			throw new BusinessException("invalid username or password");
		}
		Role role = new Role();
		role.setUserId(user.getId());
		role = this.roleDao.selectRoleByUserId(role);
		return null;
	}

	@Override
	public void logout() throws BusinessException {

	}
}
