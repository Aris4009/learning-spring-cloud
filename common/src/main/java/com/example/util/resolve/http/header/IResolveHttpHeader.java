package com.example.util.resolve.http.header;

import java.util.Map;

import com.example.exception.BusinessException;

public interface IResolveHttpHeader {
	Map<String, String> resolve() throws BusinessException;
}
