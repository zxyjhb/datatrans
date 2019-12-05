package com.yanerbo.datatransfer.support.impl.redis;

import com.yanerbo.datatransfer.support.impl.IDistributedHandle;

/**
 * reids
 * @author jihaibo
 *
 */
public class RedisDistributedHandle implements IDistributedHandle{

	@Override
	public int increment(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean lock(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean unlock(String key) {
		// TODO Auto-generated method stub
		return false;
	}

}
