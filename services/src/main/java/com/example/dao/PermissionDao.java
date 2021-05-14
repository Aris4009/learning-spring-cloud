package com.example.dao;

import org.beetl.sql.mapper.BaseMapper;

import com.example.entity.Permission;
/*
* 
* gen by beetlsql3 mapper 2021-05-14
*/
public interface PermissionDao extends BaseMapper<Permission> {

	int verifyPermissionByRoleId(Permission permission);
}
