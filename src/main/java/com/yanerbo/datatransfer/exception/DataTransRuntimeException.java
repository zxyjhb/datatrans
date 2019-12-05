package com.yanerbo.datatransfer.exception;
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
}
