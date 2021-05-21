package com.example.services;

import com.example.exception.AuthenticationException;

public interface IAuthService {
	public String refresh(String token) throws AuthenticationException;

	public void verify(String token) throws AuthenticationException;

	public void verify(String token, String url) throws AuthenticationException;
}
