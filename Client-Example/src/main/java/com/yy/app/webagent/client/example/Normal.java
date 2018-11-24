package com.yy.app.webagent.client.example;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.yy.app.webagent.client.Util;
import com.yy.app.webagent.data.DataFactory;
import com.yy.util.MapValue;
import com.yy.util.StringUtil;

public class Normal {

	public static void main(String[] args) throws ClientProtocolException, IOException {
		
		Util.mainInit();


		String url = "http://www.baidu.com";
		String method = "get";
		byte[] data = null;
		String[] headersArr = {
			"Referer", "http://image.baidu.com",
			"User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36 OPR/56.0.3051.104",
			"Cookie", "cookie1=abcdef"
		};
		MapValue headersMap = new MapValue();
		for (int i = 0, l = headersArr.length; i < l; i += 2) {
			headersMap.put(headersArr[i], headersArr[i + 1]);
		}


		// 构造请求数据。
		byte[] requestData = DataFactory.buildRequestData(url, method, headersMap, data);


		// 请求。
		String requestUrl = Util.getHost();
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost request = new HttpPost(requestUrl);
		request.setEntity(new ByteArrayEntity(requestData, ContentType.APPLICATION_OCTET_STREAM));
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
