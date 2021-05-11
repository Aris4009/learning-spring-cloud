package com.example.dao;

import org.beetl.sql.mapper.BaseMapper;

import com.example.entity.User;
/*
* 
* gen by beetlsql3 mapper 2021-05-10
*/
public interface UserDao extends BaseMapper<User> {

	User selectByUsername(User user);
}
