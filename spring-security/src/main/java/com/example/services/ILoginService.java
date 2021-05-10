package com.example.services;

import org.springframework.security.core.userdetails.UserDetails;

import com.example.entity.User;
import com.example.exception.BusinessException;

public interface ILoginService {

	public UserDetails login(User user) throws BusinessException;

	public void logout() throws BusinessException;
}
