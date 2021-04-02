package com.example.interceptor;

/**
 * 记录日志接口，可将日志记录到elasticsearch,mysql等持久化软件中
 */
public interface IStoreLog {

	/**
	 * 存储日志，需实现该方法
	 * 
	 * @param requestLog 日志对象
	 */
	void store(RequestLog requestLog);
}
