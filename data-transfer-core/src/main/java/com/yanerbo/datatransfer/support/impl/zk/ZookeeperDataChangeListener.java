package com.yanerbo.datatransfer.support.impl.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;

/**
 * 
 * @author jihaibo
 *
 */
public class ZookeeperDataChangeListener implements TreeCacheListener{

	@Override
	public void childEvent(CuratorFramework arg0, TreeCacheEvent arg1) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
