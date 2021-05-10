
sample
===
* 注释

	select #{use("cols")} from permission  where  #{use("condition")}

cols
===
	id,name,url

updateSample
===
	
	id=#{id},name=#{name},url=#{url}

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
	
	