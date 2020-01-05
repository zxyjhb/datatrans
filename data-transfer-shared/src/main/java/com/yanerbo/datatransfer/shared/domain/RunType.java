package com.yanerbo.datatransfer.shared.domain;

/**
 * 运行模式
 * @author 274818
 *
 */
public enum RunType {

	all,  //全量
	init, //正在初始化
	hook, //挂起
	add,  //增量
	none; //无
}
