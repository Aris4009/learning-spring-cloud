package com.example.entity;
/*
* 
* gen by beetlsql3 2021-03-17
*/

import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;

@Table(name = "role")
public class Role implements java.io.Serializable {

	private static final long serialVersionUID = 2301959313748022875L;

	@AssignID("snow")
	private Long id;
	private String roleName;

	public Role() {
	}

	public Role(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

}
