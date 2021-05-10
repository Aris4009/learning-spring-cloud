package com.example.entity;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;
/*
* 
* gen by beetlsql3 2021-05-10
*/

@Table(name="permission")
public class Permission implements java.io.Serializable {
	@AssignID("snow")
	private Long id ;

	private String name ;

	private String url ;

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

	public String getUrl(){
		return  url;
	}

	public void setUrl(String url ){
		this.url = url;
	}

	@Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
