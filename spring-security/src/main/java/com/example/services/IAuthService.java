package com.example.services;

import com.example.exception.BusinessException;

public interface IAuthService {
	public String refresh(String token) throws BusinessException;

	public void verify(String token) throws BusinessException;

	public void verify(String token, String url) throws BusinessException;
}
