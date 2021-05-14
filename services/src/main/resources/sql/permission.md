sample
===

* 注释

  select #{use("cols")} from permission where #{use("condition")}

cols
===

	id,name,url,gid

updateSample
===

	id=#{id},name=#{name},url=#{url},gid=#{gid}

condition
===

	1 = 1  
	-- @if(!isEmpty(id)){
	 and id=#{id}
	-- @}
	-- @if(!isEmpty(name)){
	 and name=#{name}
	-- @}
	-- @if(!isEmpty(url)){
	 and url=#{url}
	-- @}
	-- @if(!isEmpty(gid)){
	 and gid=#{gid}
	-- @}

verifyPermissionByRoleId
===

    select count(1) from permission t1, role_permission t2 where t1.id = t2.permission_id and t2.role_id=#{roleId} and t2.url = #{url} 	