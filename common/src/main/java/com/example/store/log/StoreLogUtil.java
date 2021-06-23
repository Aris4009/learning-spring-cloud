package com.example.store.log;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 持久化日志
 */
public final class StoreLogUtil {

	private static final Logger log = LoggerFactory.getLogger(StoreLogUtil.class);

	private StoreLogUtil() {
	}

	public static void storeLog(List<IStoreLog> storeLogList, ILog ilog) {
		try {
			if (storeLogList == null || ilog == null) {
				return;
			}
			for (IStoreLog storeLog : storeLogList) {
				storeLog.store(ilog);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
