package com.yy.app.webagent.client;

import java.net.URLEncoder;

import com.yy.log.Logger;
import com.yy.util.StringUtil;
import com.yy.web.config.SystemConfig;

public class Util {
	
//	public static final String hostname = "127.0.0.1";
	public static final String hostname = "dudongli.vicp.net";
	public static final int port = 92;


	/**
	 * 以 Java 应用程序单独运行时的初始化操作。
	 */
	public static void mainInit() {

		String root = System.getProperty("user.dir") + "\\";
		SystemConfig.setSystemPath(root);

		Logger.setSystemPath(root);
	}

	
	/**
	 * 获取测试的主机地址。
	 * 
	 * @return
	 */
	public static String getHost() {
		
		return getHost(null);
	}

	
	/**
	 * 获取测试的主机地址。
	 * 
	 * @return
	 */
	public static String getHost(String url) {
		
		String host = "http://" + hostname + ":" + port + "/";
		
		if (!StringUtil.isEmpty(url)) {
			try {
				return host + "?url=" + URLEncoder.encode(url, StringUtil.UTF8);
			} catch (Exception e) {
			}
		}
		
		return host;
	}
}
