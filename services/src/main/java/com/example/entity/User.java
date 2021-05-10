package com.example.entity;
import com.example.common.JSON;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;
/*
* 
* gen by beetlsql3 2021-05-10
*/

@Table(name="user")
public class User implements java.io.Serializable {
	@AssignID("snow")
	private Long id ;

	private String username ;

	private String password ;

	public Long getId(){
		return  id;
	}

	public void setId(Long id ){
		this.id = id;
	}

	public String getUsername(){
		return  username;
	}

	public void setUsername(String username ){
		this.username = username;
	}

	public String getPassword(){
		return  password;
	}

	public void setPassword(String password ){
		this.password = password;
	}

	@Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
