package com.example.utils;

import java.security.Key;
import java.security.SignatureException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import com.example.common.JSON;
import com.example.entity.JwtProp;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.exception.BusinessException;
import com.google.gson.reflect.TypeToken;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

	private final Key key;

	private final JwtProp jwtProp;

	private final HttpSession httpSession;

	private static final String CLAIM_KEY = "payload";

	private static final int SUCCESS = 0;

	private static final int EXPIRE = -1;

	private static final int INVALID = -2;

	public JwtUtils(Key key, JwtProp jwtProp, HttpSession httpSession) {
		this.key = key;
		this.jwtProp = jwtProp;
		this.httpSession = httpSession;
	}

	public <T> String sign(String id, T payload) {
		long expire = System.currentTimeMillis() + this.jwtProp.getExpire();
		String token = Jwts.builder().setIssuer(this.jwtProp.getIss()).setExpiration(new Date(expire))
				.claim(CLAIM_KEY, payload).signWith(key).compact();
		if (this.httpSession != null && !StrUtil.isBlankIfStr(id)) {
			System.out.println(this.httpSession.getId());
			String sessionKey = this.jwtProp.getSessionKey() + "-" + id;
			this.httpSession.setAttribute(sessionKey, token);
		}
		return token;
	}

	public <T> String refresh(String id, String token, T payload) throws BusinessException {
		int code = verify(id, token);
		if (code == SUCCESS) {
			return sign(id, payload);
		} else {
			throw BusinessException.paramsError("token");
		}
	}

	public int verify(String id, String token) {
		int code = SUCCESS;
		try {
			parseClaimsJws(token);
			if (this.httpSession != null && !StrUtil.isBlankIfStr(id)) {
				String sessionKey = this.jwtProp.getSessionKey() + "-" + id;
				String session = String.valueOf(this.httpSession.getAttribute(sessionKey));
				if (!StrUtil.equals(token, session)) {
					throw new SignatureException("redis session " + sessionKey + " invalid");
				}
			}
		} catch (ExpiredJwtException signatureException) {
			code = EXPIRE;
		} catch (Exception e) {
			code = INVALID;
		}
		return code;
	}

	public <T> T parse(String token, TypeToken<T> typeToken) {
		String json = parseClaimsJws(token).getBody().get(CLAIM_KEY).toString();
		return JSON.parse(json, typeToken);
	}

	public void removeSession(String id) {
		if (this.httpSession != null && !StrUtil.isBlankIfStr(id)) {
			String sessionKey = this.jwtProp.getSessionKey() + "-" + id;
			System.out.println(this.httpSession.getAttribute(sessionKey));
			this.httpSession.removeAttribute(sessionKey);
			System.out.println(this.httpSession.getId());
			System.out.println(this.httpSession.getAttribute(sessionKey));
			this.httpSession.invalidate();
			System.out.println(this.httpSession.getAttribute(sessionKey));
		}
	}

	private Jws<Claims> parseClaimsJws(String token) {
		return Jwts.parserBuilder().setSigningKey(this.key).setAllowedClockSkewSeconds(this.jwtProp.getAfterExpire())
				.build().parseClaimsJws(token);
	}

	public static void main(String[] args) throws BusinessException, InterruptedException {
		Key key = Keys.hmacShaKeyFor(SecureUtil.sha1("111").getBytes());
		JwtProp jwtProp = new JwtProp();
		jwtProp.setIss("aa");
		jwtProp.setExpire(1 * 1000);
		jwtProp.setAfterExpire(10);
		JwtUtils jwtUtils = new JwtUtils(key, jwtProp, null);

		User user = new User();
		user.setId(1L);
		user.setUsername("111111");
		Role role = new Role();
		role.setId(1L);
		role.setName("管理员");
		Map<String, Object> map = new HashMap<>();
		map.put("user", user);
		map.put("role", role);

		String sign = jwtUtils.sign(user.getId().toString(), map);
		System.out.println(sign);
		// Thread.sleep(6000);
		System.out.println(jwtUtils.parseClaimsJws(sign).getBody().get("payload").toString());
		System.out.println(jwtUtils.verify(String.valueOf(user.getId()), sign));
	}
}
