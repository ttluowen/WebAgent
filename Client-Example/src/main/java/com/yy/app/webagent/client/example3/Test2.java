package com.yy.app.webagent.client.example3;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;

import com.yy.app.webagent.client.Util;
import com.yy.util.StringUtil;

public class Test2 {

	public static void main(String[] args) throws ClientProtocolException, IOException {
		
		Util.mainInit();

		
		System.setProperty("proxySet", "true");
		System.setProperty("http.proxyHost", "58.53.128.83");
		System.setProperty("http.proxyPort", "3128");
		System.setProperty("http.proxyUser", "someUserName");
		System.setProperty("http.proxyPassword", "somePassword");

		System.setProperty("https.proxyHost", "118.190.94.224");
		System.setProperty("https.proxyPort", "9001");
		
		System.setProperty("socksProxyHost", "192.168.1.213");
		System.setProperty("socksProxyPort", "7171");
		

		String url = "http://2018.ip138.com/ic.asp";
//		String url = "http://www.baidu.com";

		
		// 初始化proxy对象
		Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("112.98.126.100", 41578));
		  
		// 创建连接
		URL u = new URL(url);
		URLConnection conn = u.openConnection(proxy);
		String content = IOUtils.toString(conn.getInputStream(), StringUtil.GBK);
		System.out.println(content);


//		// 请求。
////		String requestUrl = Util.getHost(url);
//		String requestUrl = url;
//		CloseableHttpClient httpClient = HttpClients.createDefault();
//		HttpGet request = new HttpGet(requestUrl);
//		CloseableHttpResponse response = httpClient.execute(request);
//
//		
//		// 获取响应内容。
//		int statusCode = response.getStatusLine().getStatusCode();
//		Header[] headers = response.getAllHeaders();
//		String content = IOUtils.toString(response.getEntity().getContent(), StringUtil.GBK);
//
//		
//		// 打印。
//		System.out.println("url: " + requestUrl);
//		System.out.println("statuscode: " + statusCode);
//		System.out.println("headers: ");
//		for (Header header : headers) {
//			System.out.println("    " + header.getName() + ": " + header.getValue());
//		}
//		System.out.println("content: " + content);
	}
}
