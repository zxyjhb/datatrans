//package com.yanerbo.datatransfer.config;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import org.apache.curator.framework.CuratorFramework;
//import org.apache.curator.framework.recipes.cache.TreeCache;
//import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
//import org.apache.curator.framework.recipes.cache.TreeCacheListener;
//import org.apache.curator.utils.ZKPaths;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Component;
//import com.alibaba.fastjson.JSONObject;
//import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
//import com.google.common.base.Charsets;
//import com.yanerbo.datatransfer.shared.domain.DataTrans;
//import com.yanerbo.datatransfer.shared.util.Constant;
//import com.yanerbo.datatransfer.support.util.DataTransContext;
///**
// * 
// * @author jihaibo
// *
// */
//@Component
//public class DataTransConfig implements InitializingBean, Constant{
//	
//	/**
//	 * zk
//	 */
//	@Autowired
//	@Qualifier("zookeeperRegistryCenter")
//	private ZookeeperRegistryCenter regCenter;
//	/**
//	 * 本地数据
//	 */
//	private Map<String, DataTrans> dataTransMap = new HashMap<String, DataTrans>();
//	
//	/**
//	 * 获取配置信息列表
//	 * @return
//	 */
//	public Map<String, DataTrans> getDataTransConfigs() {
//		return dataTransMap;
//	}
//	
//	/**
//	 * 获取分片配置信息
//	 * @param jobName
//	 * @return
//	 */
//	public DataTrans getLikeDataTrans(String name) {
//		
//		for(Entry<String, DataTrans> entry : dataTransMap.entrySet()) {
//			if(name.startsWith(entry.getKey())) {
//				return entry.getValue();
//			}
//		}
//		return null;
//	}
//	
//	/**
//	 * 获取分片配置信息
//	 * @param jobName
//	 * @return
//	 */
//	public DataTrans getDataTrans(String name) {
//		return dataTransMap.get(name);
//	}
//	
//	/**
//	 * 初始化加载数据
//	 */
//	@Override
//	public void afterPropertiesSet() throws Exception {
//		//监听config目录
//		for(String key : regCenter.getChildrenKeys(CONFIG_ROOT)) {
//			regCenter.addCacheData(String.format(CONFIG_PATH, key));
//			TreeCache cache = (TreeCache) regCenter.getRawCache(String.format(CONFIG_PATH, key));
//			cache.getListenable().addListener(treeCacheListener);
//			
//		}
//	}
//	
//	
//	TreeCacheListener treeCacheListener = new TreeCacheListener() {
//		/**
//		 * 日志
//		 */
//		final Logger log = LoggerFactory.getLogger(TreeCacheListener.class);
//		
//        @Override
//        public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
//        	
//            switch (event.getType()) {
//                case NODE_ADDED:{
//                	DataTrans dataTrans = JSONObject.parseObject(new String(client.getData().forPath(event.getData().getPath()), Charsets.UTF_8), DataTrans.class);
//                	dataTransMap.put(ZKPaths.getNodeFromPath(event.getData().getPath()), dataTrans);
//                	log.info("add node: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
//                	break;
//                }
//                case NODE_REMOVED:{
//                	dataTransMap.remove(ZKPaths.getNodeFromPath(event.getData().getPath()));
//                	log.info("remove node: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
//                	break;
//                }
//                case NODE_UPDATED:{
//                	DataTrans dataTrans = JSONObject.parseObject(new String(client.getData().forPath(event.getData().getPath()), Charsets.UTF_8), DataTrans.class);
//                	dataTransMap.put(ZKPaths.getNodeFromPath(event.getData().getPath()), dataTrans);
//                	log.info("update node: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
//                	break;
//                }
//			default:
//				break;
//
//            }
//        }
//    };
//
//	
//}