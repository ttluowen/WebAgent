package com.yy.app.webagent.server.exception;

/**
 * 不支持的 Method 请求类型。
 * 
 * @since 2018-11-23
 * @version 1.0
 * @author Luowen
 */
public class UnsupportedMethodTypeException extends Exception {

	private static final String TEXT = "不支持的 Method 请求类型";
	private static final long serialVersionUID = 1L;

	public UnsupportedMethodTypeException() {
		this(TEXT);
	}

	public UnsupportedMethodTypeException(String message) {
		super(TEXT + message);
	}

	public UnsupportedMethodTypeException(Throwable cause) {
		super(cause);
	}

	public UnsupportedMethodTypeException(String message, Throwable cause) {
		super(TEXT + message, cause);
	}

	public UnsupportedMethodTypeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(TEXT + message, cause, enableSuppression, writableStackTrace);
	}

}
