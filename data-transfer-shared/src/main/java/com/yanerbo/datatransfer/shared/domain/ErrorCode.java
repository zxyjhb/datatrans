package com.yanerbo.datatransfer.shared.domain;

public enum ErrorCode {

	
	ERR001(""),
	ERR002(""),
	ERR003("无效的分页方式"),
	ERR004("发送RcoketMQ失败"),
	ERR005("数据库解密失败");
	
	public String msg;
	
	ErrorCode(String msg){
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
}
