package com.yanerbo.datatransfer.exception;

import com.yanerbo.datatransfer.shared.domain.ErrorCode;

/**
 * 运行异常类
 * @author jihaibo
 *
 */
public class DataTransRuntimeException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1139310934226675367L;
	
	/**
	 * 
	 * @param message
	 */
	public DataTransRuntimeException(String message) {
		super(message);
	}
	
	/**
	 * 
	 * @param message
	 */
	public DataTransRuntimeException(ErrorCode code) {
		super(code.getMsg());
	}

	/**
	 * 
	 * @param message
	 */
	public DataTransRuntimeException(ErrorCode code, Throwable cause) {
		super(code.getMsg(), cause);
	}
}
