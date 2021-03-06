package com.example.services;

import com.example.entity.User;
import com.example.entity.UserDetail;
import com.example.exception.AuthenticationException;
import com.example.exception.BusinessException;

public interface ILoginService {

	public UserDetail login(User user) throws BusinessException;

	public void logout(String token) throws AuthenticationException;

}
