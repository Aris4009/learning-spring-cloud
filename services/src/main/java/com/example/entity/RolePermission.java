package com.example.entity;

import org.beetl.sql.annotation.entity.Auto;
import org.beetl.sql.annotation.entity.Table;

import com.example.common.JSON;
/*
* 
* gen by beetlsql3 2021-05-10
*/

@Table(name = "role_permission")
public class RolePermission implements java.io.Serializable {

	private static final long serialVersionUID = 2112567820102616591L;

	@Auto
	private Long id;

	private Long roleId;

	private Long permissionId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(Long permissionId) {
		this.permissionId = permissionId;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
