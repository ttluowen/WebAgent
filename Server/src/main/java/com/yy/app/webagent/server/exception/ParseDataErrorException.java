package com.yy.app.webagent.server.exception;

/**
 * 解析数据失败的错误。
 * 
 * @since 2018-11-23
 * @version 1.0
 * @author Luowen
 */
public class ParseDataErrorException extends Exception {

	private static final long serialVersionUID = 1L;

	public ParseDataErrorException() {

		this("解析数据失败");
	}

	public ParseDataErrorException(String message) {
		super(message);
	}

	public ParseDataErrorException(Throwable cause) {
		super(cause);
	}

	public ParseDataErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParseDataErrorException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
