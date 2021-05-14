
sample
===
* 注释

	select #{use("cols")} from permission_group  where  #{use("condition")}

cols
===
	gid,gname

updateSample
===
	
	gid=#{gid},gname=#{gname}

condition
===

	1 = 1  
	-- @if(!isEmpty(gid)){
	 and gid=#{gid}
	-- @}
	-- @if(!isEmpty(gname)){
	 and gname=#{gname}
	-- @}
	
	