package com.example.dao;

import org.beetl.sql.mapper.BaseMapper;

import com.example.entity.Role;
/*
* 
* gen by beetlsql3 mapper 2021-05-10
*/
public interface RoleDao extends BaseMapper<Role> {
	Role selectRoleByUserId(Role role);
}
