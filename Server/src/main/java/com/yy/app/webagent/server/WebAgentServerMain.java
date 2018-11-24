package com.yy.app.webagent.server;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

import com.yy.log.Logger;
import com.yy.util.NumberUtil;
import com.yy.web.config.SystemConfig;

public class WebAgentServerMain {
	
	private static final int PORT = 92;
	
	
	/**
	 * 以 Java 应用程序单独运行时的初始化操作。
	 */
	public static void mainInit() {

		String root = System.getProperty("user.dir") + "\\";
		SystemConfig.setSystemPath(root);
		SystemConfig.setWebInfPath(root + "WEB-INF\\");
		
		SystemConfig sys = new SystemConfig(null);
		sys.initSystemConfig();

		Logger.setSystemPath(root);
	}

	
	/**
	 * 执行入口。
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		mainInit();
		
		int port = NumberUtil.parseInt(SystemConfig.getConfig("prop"));
		if (port == 0) {
			port = PORT;
		}


		Server server = new Server();
		server.setStopAtShutdown(true);
		server.setHandler(new RequestHandler());

		ServerConnector connector1 = new ServerConnector(server);
		connector1.setPort(port);
		server.setConnectors(new Connector[] { connector1 });
		
		
		Logger.log("启动服务，端口 " + port);
		

		server.start();
		server.join();
	}
}
