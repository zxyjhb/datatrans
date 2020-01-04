package com.yanerbo.datatransfer.shared.domain;

/**
 * 
 * @author 274818
 *
 */
public enum PageType {

	post,  //位置分页不分片
	post_sharding, //位置分页，分片
	seq,   //顺序分页，不分片
	seq_sharding; //顺序分页，分片
}
