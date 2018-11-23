package com.yy.app.webagent.server;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpParams;

import com.yy.app.webagent.data.RequestDataStruct;
import com.yy.app.webagent.data.RequestDataStruct.ParamsStruct;
import com.yy.app.webagent.server.exception.ParseDataErrorException;
import com.yy.app.webagent.server.exception.UnsupportedMethodException;
import com.yy.util.MapValue;
import com.yy.util.StringUtil;
import com.yy.web.request.annotation.Method;

@SuppressWarnings("deprecation")
public class ProxyServer {
	
	/**
	 * GET 请求。
	 * 
	 * @param url
	 * @param headers
	 * @return
	 * @throws UnsupportedMethodException 
	 * @throws IOException 
	 * @throws UnsupportedOperationException 
	 */
	public static ResponseData doGet(String url, MapValue headers) throws UnsupportedOperationException, IOException, UnsupportedMethodException {

		return request(Method.GET.value(), null, url, headers);
	}

	
	/**
	 * POST 请求。
	 * 
	 * @param url
	 * @param headers
	 * @param data
	 * @return
	 * @throws UnsupportedMethodException 
	 * @throws IOException 
	 * @throws UnsupportedOperationException 
	 */
	public static ResponseData doPost(String url, MapValue headers, byte[] data) throws UnsupportedOperationException, IOException, UnsupportedMethodException {

		return request(Method.POST.value(), null, url, headers);
	}
	
	
	/**
	 * 代理请求。
	 * 
	 * @param requestData
	 * @throws ParseDataErrorException
	 * @throws UnsupportedMethodException
	 * @throws IOException 
	 * @throws UnsupportedOperationException 
	 */
	public static void request(RequestDataStruct requestData) throws ParseDataErrorException, UnsupportedMethodException, UnsupportedOperationException, IOException {
		
		ParamsStruct params = requestData.getParams();
		if (params == null) {
			throw new ParseDataErrorException();
		}

		
		String url = params.getUrl();
		String method = params.getMethod();
		MapValue headers = params.getHeaders();
		byte[] data = requestData.getData();
		
		
		if (method.equalsIgnoreCase(Method.GET.value())) {
			doGet(url, headers);
		} else if (method.equalsIgnoreCase(Method.POST.value())) {
			doPost(url, headers, data);
		} else {
			throw new UnsupportedMethodException();
		}
	}


	/**
	 * 获取指定页面的文本内容。
	 * @param method TODO
	 * @param httpParams
	 * @param url
	 * @param headers
	 * 
	 * @return
	 * @throws IOException 
	 * @throws UnsupportedOperationException 
	 * @throws UnsupportedMethodException 
	 */
	private static ResponseData request(String method, MapValue httpParams, String url, MapValue headers) throws UnsupportedOperationException, IOException, UnsupportedMethodException {

		CloseableHttpClient client = HttpClients.createDefault();

		try {
			ResponseData responseData = new ResponseData();
			Header[] responseHeaders = null;
			byte[] responseDataByte;


			url = url.replaceAll(" ", "%20");


			// 设置客户端额外参数。
			if (httpParams != null && httpParams.isNotEmpty()) {
				HttpParams params = client.getParams();
				Iterator<Entry<String, Object>> entries = httpParams.entrySet().iterator();
				
				while (entries.hasNext()) {
					Entry<String, Object> entry = entries.next();
					params.setParameter(entry.getKey(), entry.getValue());
				}
			}


			// 实例化请求类型。
			HttpRequestBase request = null;
			if (method.equalsIgnoreCase(Method.GET.value())) {
				request = new HttpGet(url);
			} else if (method.equalsIgnoreCase(Method.POST.value())){
				request = new HttpPost(url);
			} else {
				throw new UnsupportedMethodException();
			}
			

			// 设置 Header 信息。
			if (headers != null) {
				Iterator<String> headerNames = headers.keySet().iterator();
				while (headerNames.hasNext()) {
					String name = headerNames.next();
					String value = headers.getString(name);
					if (!StringUtil.isEmpty(value)) {
						request.addHeader(name, value);
					}
				}
			}
			

			// 发送请求。
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			responseHeaders = response.getAllHeaders();
			
			
			// 获取响应状态码。
			responseData.setStatus(response.getStatusLine().getStatusCode());
			
			
			// 获取解码方式。
			for (Header header : responseHeaders) {
				if (header.getName().equals("Content-Encoding")) {
					if (header.getValue().toLowerCase().equals("gzip")) {
						entity = new GzipDecompressingEntity(entity);
					}
				}
			}

			
			// 获取响应内容。
			responseDataByte = IOUtils.toByteArray(entity.getContent());


			responseData.setHeaders(formatHeaders(responseHeaders));
			responseData.setData(responseDataByte);
			
			
			return responseData;
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}


	/**
	 * 转换 Header 数据类型。
	 * 
	 * @param headers
	 * @return
	 */
	private static MapValue formatHeaders(Header[] headers) {
		
		MapValue map = new MapValue();
		
		if (headers != null) {
			for (Header header : headers) {
				map.put(header.getName(), header.getValue());
			}
		}
		
		
		return map;
	}
}
