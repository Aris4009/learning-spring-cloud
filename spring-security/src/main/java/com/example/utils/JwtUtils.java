package com.example.utils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.entity.JwtProp;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.exception.BusinessException;

import cn.hutool.crypto.SecureUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

	private Key key;

	private JwtProp jwtProp;

	private static final String CLAIM_KEY = "payload";

	private static final int SUCCESS = 0;

	private static final int EXPIRE = -1;

	private static final int INVALID = -2;

	public JwtUtils(Key key, JwtProp jwtProp) {
		this.key = key;
		this.jwtProp = jwtProp;
	}

	public <T> String sign(T payload) {
		long expire = System.currentTimeMillis() + this.jwtProp.getExpire();
		return Jwts.builder().setIssuer(this.jwtProp.getIss()).setExpiration(new Date(expire)).claim(CLAIM_KEY, payload)
				.signWith(key).compact();
	}

	public <T> String refresh(String token) {
		int code = verify(token);
		if (code != EXPIRE) {

		}
	}

	public int verify(String token) {
		int code = SUCCESS;
		try {
			parseClaimsJws(token);
		} catch (ExpiredJwtException signatureException) {
			code = EXPIRE;
		} catch (Exception e) {
			code = INVALID;
		}
		return code;
	}

	public Object parse(String token) {
		return parseClaimsJws(token).getBody().get(CLAIM_KEY);
	}

	private Jws<Claims> parseClaimsJws(String token) {
		return Jwts.parserBuilder().setSigningKey(this.key).build().parseClaimsJws(token);
	}

	public static void main(String[] args) throws BusinessException, InterruptedException {
		Key key = Keys.hmacShaKeyFor(SecureUtil.sha1("111").getBytes());
		JwtProp jwtProp = new JwtProp();
		jwtProp.setIss("aa");
		jwtProp.setExpire(5 * 1000);
		JwtUtils jwtUtils = new JwtUtils(key, jwtProp);

		User user = new User();
		user.setId(1L);
		user.setUsername("111111");
		Role role = new Role();
		role.setId(1L);
		role.setName("管理员");
		Map<String, Object> map = new HashMap<>();
		map.put("user", user);
		map.put("role", role);

		String sign = jwtUtils.sign(map);
		System.out.println(sign);
		System.out.println(jwtUtils.parseClaimsJws(sign).getBody().get("payload"));
		System.out.println(jwtUtils.verify(null));
	}
}
