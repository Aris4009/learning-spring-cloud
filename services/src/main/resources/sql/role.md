sample
===

* 注释

  select #{use("cols")} from role where #{use("condition")}

cols
===

	id,name

updateSample
===

	id=#{id},name=#{name}

condition
===

	1 = 1  
	-- @if(!isEmpty(id)){
	 and id=#{id}
	-- @}
	-- @if(!isEmpty(name)){
	 and name=#{name}
	-- @}

selectRoleByUserId
===

	select t1.id,t1.name from role t1,user_role t2 where t1.id = t2.role_id and t2.user_id=#{userId}