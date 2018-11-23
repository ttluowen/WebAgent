package com.yy.app.webagent.server.exception;

/**
 * 不支持的 Method 请求类型。
 * 
 * @since 2018-11-23
 * @version 1.0
 * @author Luowen
 */
public class UnsupportedMethodException extends Exception {

	private static final long serialVersionUID = 1L;

	public UnsupportedMethodException() {
		this("不支持的 Method 请求类型");
	}

	public UnsupportedMethodException(String message) {
		super(message);
	}

	public UnsupportedMethodException(Throwable cause) {
		super(cause);
	}

	public UnsupportedMethodException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedMethodException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
