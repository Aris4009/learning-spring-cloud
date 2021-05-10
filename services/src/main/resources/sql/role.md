
sample
===
* 注释

	select #{use("cols")} from role  where  #{use("condition")}

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
	
	