package com.example.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.dao.UserDao;
import com.example.entity.User;
import com.example.exception.BusinessException;

@Service
public class UserService {

	private UserDao userDao;

	public UserService(UserDao userDao) {
		this.userDao = userDao;
	}

	public List<User> list(User user) throws BusinessException {
		if (user == null || user.getUsername() == null) {
			throw BusinessException.paramsMustBeNotEmptyOrNullError("username");
		}
		return userDao.list(user);
	}
}
