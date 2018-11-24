package com.yy.app.webagent.client.example;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.yy.app.webagent.client.Util;
import com.yy.util.StringUtil;

public class Simple {

	public static void main(String[] args) throws ClientProtocolException, IOException {
		
		Util.mainInit();


		String url = "http://www.baidu.com";


		// 请求。
		String requestUrl = Util.getHost(url);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet request = new HttpGet(requestUrl);
		CloseableHttpResponse response = httpClient.execute(request);

		
		// 获取响应内容。
		int statusCode = response.getStatusLine().getStatusCode();
		Header[] headers = response.getAllHeaders();
		String content = IOUtils.toString(response.getEntity().getContent(), StringUtil.UTF8);

		
		// 打印。
		System.out.println("url: " + requestUrl);
		System.out.println("statuscode: " + statusCode);
		System.out.println("headers: ");
		for (Header header : headers) {
			System.out.println("    " + header.getName() + ": " + header.getValue());
		}
		System.out.println("content: " + content);
	}
}
