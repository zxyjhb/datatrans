package com.yanerbo.datatransfer.entity;

public enum ErrorCode {

	
	ERR001(""),
	ERR002(""),
	ERR003("无效的分页方式");
	
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
