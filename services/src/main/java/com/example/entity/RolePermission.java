package com.example.entity;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;
/*
* 
* gen by beetlsql3 2021-05-10
*/

@Table(name="role_permission")
public class RolePermission implements java.io.Serializable {
	@AutoId
	private Long id ;

	private Long roleId ;

	private Long permissionId ;

	public Long getId(){
		return  id;
	}

	public void setId(Long id ){
		this.id = id;
	}

	public Long getRoleId(){
		return  roleId;
	}

	public void setRoleId(Long roleId ){
		this.roleId = roleId;
	}

	public Long getPermissionId(){
		return  permissionId;
	}

	public void setPermissionId(Long permissionId ){
		this.permissionId = permissionId;
	}

	@Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
