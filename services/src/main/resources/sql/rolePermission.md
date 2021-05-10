
sample
===
* 注释

	select #{use("cols")} from role_permission  where  #{use("condition")}

cols
===
	id,role_id,permission_id

updateSample
===
	
	id=#{id},role_id=#{roleId},permission_id=#{permissionId}

condition
===

	1 = 1  
	-- @if(!isEmpty(id)){
	 and id=#{id}
	-- @}
	-- @if(!isEmpty(roleId)){
	 and role_id=#{roleId}
	-- @}
	-- @if(!isEmpty(permissionId)){
	 and permission_id=#{permissionId}
	-- @}
	
	