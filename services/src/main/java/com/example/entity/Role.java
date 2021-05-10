package com.example.entity;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;
/*
* 
* gen by beetlsql3 2021-05-10
*/

@Table(name="role")
public class Role implements java.io.Serializable {
	@AssignID("snow")
	private Long id ;

	private String name ;

	public Long getId(){
		return  id;
	}

	public void setId(Long id ){
		this.id = id;
	}

	public String getName(){
		return  name;
	}

	public void setName(String name ){
		this.name = name;
	}

	@Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
