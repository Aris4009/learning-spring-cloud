package com.example.entity;

public class JwtProp {

	private String iss;

	private long expire;

	private long afterExpire;

	private String sessionKey;

	public String getIss() {
		return iss;
	}

	public void setIss(String iss) {
		this.iss = iss;
	}

	public long getExpire() {
		return expire;
	}

	public void setExpire(long expire) {
		this.expire = expire;
	}

	public long getAfterExpire() {
		return afterExpire;
	}

	public void setAfterExpire(long afterExpire) {
		this.afterExpire = afterExpire;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
}
