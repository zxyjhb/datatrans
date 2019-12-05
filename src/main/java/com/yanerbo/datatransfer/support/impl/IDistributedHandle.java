package com.yanerbo.datatransfer.support.impl;

public interface IDistributedHandle {
	
	public int increment(String key);
	
	public boolean lock(String key);
	
	public boolean unlock(String key);

	

}
