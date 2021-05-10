package com.example.config;

import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.entity.JwtProp;

import cn.hutool.crypto.SecureUtil;
import io.jsonwebtoken.security.Keys;

@Configuration
public class JwtConfig {

	@Bean
	public Key key(@Value("{jwt.key}") String key) {
		return Keys.hmacShaKeyFor(SecureUtil.sha1(key).getBytes());
	}

	@Bean
	public JwtProp jwtProp(@Value("${jwt.iss}") String iss, @Value("${jwt.expire}") long expire) {
		JwtProp jwtProp = new JwtProp();
		jwtProp.setIss(iss);
		jwtProp.setExpire(expire * 1000L);
		return jwtProp;
	}
}
