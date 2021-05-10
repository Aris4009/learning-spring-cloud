package com.example.entity;

import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;

import com.example.common.JSON;
/*
* 
* gen by beetlsql3 2021-05-10
*/

@Table(name = "role")
public class Role implements java.io.Serializable {

	private static final long serialVersionUID = 4222972544076035671L;

	@AssignID("snow")
	private Long id;

	private String name;

	private Long userId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
