
sample
===
* 注释

	select #{use("cols")} from user  where  #{use("condition")}

cols
===
	id,username,password

updateSample
===
	
	id=#{id},username=#{username},password=#{password}

condition
===

	1 = 1  
	-- @if(!isEmpty(id)){
	 and id=#{id}
	-- @}
	-- @if(!isEmpty(username)){
	 and username=#{username}
	-- @}
	-- @if(!isEmpty(password)){
	 and password=#{password}
	-- @}
	
	