package com.example.entity;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;
/*
* 
* gen by beetlsql3 2021-05-10
*/

@Table(name="user_role")
public class UserRole implements java.io.Serializable {
	@AutoId
	private Long id ;

	private Long userId ;

	private Long roleId ;

	public Long getId(){
		return  id;
	}

	public void setId(Long id ){
		this.id = id;
	}

	public Long getUserId(){
		return  userId;
	}

	public void setUserId(Long userId ){
		this.userId = userId;
	}

	public Long getRoleId(){
		return  roleId;
	}

	public void setRoleId(Long roleId ){
		this.roleId = roleId;
	}

	@Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
