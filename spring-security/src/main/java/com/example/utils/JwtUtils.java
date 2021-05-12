package com.example.utils;

import java.security.Key;
import java.security.SignatureException;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import com.example.common.JSON;
import com.example.entity.JwtProp;
import com.example.exception.BusinessException;
import com.google.gson.reflect.TypeToken;

import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

@Component
public class JwtUtils {

	private final Key key;

	private final JwtProp jwtProp;

	private final HttpSession httpSession;

	private static final String CLAIM_KEY = "payload";

	private static final String SESSION_KEY_SPLIT = ":";

	private static final int SUCCESS = 0;

	private static final int EXPIRE = -1;

	private static final int INVALID = -2;

	public JwtUtils(Key key, JwtProp jwtProp, HttpSession httpSession) {
		this.key = key;
		this.jwtProp = jwtProp;
		this.httpSession = httpSession;
	}

	public <T> String sign(T payload) {
		long expire = System.currentTimeMillis() + this.jwtProp.getExpire();
		String token = Jwts.builder().setIssuer(this.jwtProp.getIss()).setExpiration(new Date(expire))
				.claim(CLAIM_KEY, payload).signWith(key).compact();
		if (this.httpSession != null) {
			String sessionKey = this.jwtProp.getSessionKey() + SESSION_KEY_SPLIT + this.sessionId();
			this.httpSession.setAttribute(sessionKey, token);
		}
		return token;
	}

	public <T> String refresh(String token, T payload) throws BusinessException {
		int code = verify(token);
		if (code == SUCCESS) {
			return sign(payload);
		} else {
			throw BusinessException.paramsError("token");
		}
	}

	public int verify(String token) {
		int code = SUCCESS;
		try {
			parseClaimsJws(token);
			if (this.httpSession != null) {
				String sessionKey = this.jwtProp.getSessionKey() + SESSION_KEY_SPLIT + this.sessionId();
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

	public void removeSession() {
		if (this.httpSession != null) {
			String sessionKey = this.jwtProp.getSessionKey() + SESSION_KEY_SPLIT + this.sessionId();
			this.httpSession.removeAttribute(sessionKey);
			this.httpSession.invalidate();
		}
	}

	private Jws<Claims> parseClaimsJws(String token) {
		return Jwts.parserBuilder().setSigningKey(this.key).setAllowedClockSkewSeconds(this.jwtProp.getAfterExpire())
				.build().parseClaimsJws(token);
	}

	private String sessionId() {
		return this.httpSession.getId();
	}
}
