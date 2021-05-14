package com.example.entity;

import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;

import com.example.common.JSON;
/*
* 
* gen by beetlsql3 2021-05-14
*/

@Table(name = "permission")
public class Permission implements java.io.Serializable {

	private static final long serialVersionUID = -3169076214173181566L;

	@AssignID("snow")
	private Long id;

	private String name;

	private String url;

	private Long gid;

	private Long roleId;

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getGid() {
		return gid;
	}

	public void setGid(Long gid) {
		this.gid = gid;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
