package com.example.interceptor;

import java.util.List;

/**
 * 持久化日志
 */
public final class StoreLogUtil {

	private StoreLogUtil() {
	}

	public static void storeLog(List<IStoreLog> storeLogList, RequestLog requestLog) {
		if (storeLogList == null || requestLog == null) {
			return;
		}
		for (IStoreLog storeLog : storeLogList) {
			storeLog.store(requestLog);
		}
	}
}
