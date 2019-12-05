package com.yanerbo.datatransfer.support.impl.zk;

import java.util.HashMap;
import java.util.Map;

import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.yanerbo.datatransfer.support.impl.IDistributedHandle;

/**
 * zk
 * @author jihaibo
 *
 */
@Component
public class ZookeeperDistributedHandle implements IDistributedHandle{
	
	/**
	 * 
	 */
	private final static String distatomicint_path = "/page";
	
	@Autowired
	@Qualifier("zookeeperRegistryCenter")
	private ZookeeperRegistryCenter regCenter;
	
	private Map<String, DistributedAtomicInteger> atomicIntegerMap = new HashMap<String, DistributedAtomicInteger>();

	@Override
	public int increment(String key) {
		
		try {
			
			DistributedAtomicInteger atomicInteger = new DistributedAtomicInteger(regCenter.getClient(), key.concat(distatomicint_path), new RetryNTimes(3, 1000));
			AtomicValue<Integer> rc = atomicInteger.increment();
			return rc.postValue();
		}catch(Exception e) {
			e.printStackTrace();
		}
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
