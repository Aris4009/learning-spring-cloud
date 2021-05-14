package com.example.entity;

import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;

import com.example.common.JSON;
/*
* 
* gen by beetlsql3 2021-05-14
*/

@Table(name = "permission_group")
public class PermissionGroup implements java.io.Serializable {

	private static final long serialVersionUID = 5205915571352846249L;

	@AssignID("snow")
	private Long gid;

	private String gname;

	public Long getGid() {
		return gid;
	}

	public void setGid(Long gid) {
		this.gid = gid;
	}

	public String getGname() {
		return gname;
	}

	public void setGname(String gname) {
		this.gname = gname;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
