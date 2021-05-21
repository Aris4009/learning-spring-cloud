package com.example.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.dao.RoleDao;
import com.example.dao.UserDao;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.entity.UserDetail;
import com.example.exception.AuthenticationException;
import com.example.exception.BusinessException;
import com.example.utils.JwtUtils;

public abstract class AbstractUserPasswordLoginService implements ILoginService {

	private final UserDao userDao;

	private final RoleDao roleDao;

	private final JwtUtils jwtUtils;

	public AbstractUserPasswordLoginService(UserDao userDao, RoleDao roleDao, JwtUtils jwtUtils) {
		this.userDao = userDao;
		this.roleDao = roleDao;
		this.jwtUtils = jwtUtils;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public RoleDao getRoleDao() {
		return roleDao;
	}

	public JwtUtils getJwtUtils() {
		return jwtUtils;
	}

	@Override
	public UserDetail login(User user) throws BusinessException {
		preLogin(user);
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
		afterLogin(userDetail);
		return userDetail;
	}

	@Override
	public void logout(String token) throws AuthenticationException {
		preLogout();
		this.jwtUtils.removeSession(token);
		afterLogout();
	}

	protected abstract void preLogin(User user) throws BusinessException;

	protected abstract UserDetail afterLogin(UserDetail userDetail);

	protected abstract void preLogout();

	protected abstract void afterLogout();
}
