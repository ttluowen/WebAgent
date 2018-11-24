package com.yy.app.webagent.server;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpParams;

import com.yy.app.webagent.data.RequestDataStruct;
import com.yy.app.webagent.data.RequestDataStruct.ParamsStruct;
import com.yy.app.webagent.server.exception.ParseDataErrorException;
import com.yy.app.webagent.server.exception.UnsupportedMethodTypeException;
import com.yy.log.Logger;
import com.yy.util.FileUtil;
import com.yy.util.MapValue;
import com.yy.util.NumberUtil;
import com.yy.util.StringUtil;
import com.yy.web.config.SystemConfig;
import com.yy.web.request.annotation.Method;

@SuppressWarnings("deprecation")
public class ProxyFactory {
	
	/** 默认请求头信息。 */
	private static MapValue defaultRequestHeaders;
	
	
	/**
	 * 获取默认请求头信息。
	 * 
	 * @return
	 */
	private static MapValue getDefaultRequestHeaders() {

		if (defaultRequestHeaders == null) {
			MapValue headers = new MapValue();
			headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36 OPR/56.0.3051.104");
		}


		return defaultRequestHeaders;
	}
	
	
	/**
	 * GET 请求。
	 * 
	 * @param url
	 * @param headers
	 * @return
	 * @throws UnsupportedMethodTypeException 
	 * @throws IOException 
	 * @throws UnsupportedOperationException 
	 */
	public static ResponseData doGet(String url, MapValue headers) throws UnsupportedOperationException, IOException, UnsupportedMethodTypeException {

		return request(Method.GET.value(), null, url, headers, null);
	}

	
	/**
	 * POST 请求。
	 * 
	 * @param url
	 * @param headers
	 * @param data
	 * @return
	 * @throws UnsupportedMethodTypeException 
	 * @throws IOException 
	 * @throws UnsupportedOperationException 
	 */
	public static ResponseData doPost(String url, MapValue headers, byte[] data) throws UnsupportedOperationException, IOException, UnsupportedMethodTypeException {

		return request(Method.POST.value(), null, url, headers, data);
	}
	
	
	/**
	 * 代理请求。
	 * 
	 * @param requestData
	 * @throws ParseDataErrorException
	 * @throws UnsupportedMethodTypeException
	 * @throws IOException 
	 * @throws UnsupportedOperationException 
	 */
	public static ResponseData request(RequestDataStruct requestData) throws ParseDataErrorException, UnsupportedMethodTypeException, UnsupportedOperationException, IOException {
		
		ParamsStruct params = requestData.getParams();
		if (params == null) {
			throw new ParseDataErrorException();
		}

		
		String url = params.getUrl();
		String method = params.getMethod();
		MapValue headers = params.getHeaders();
		byte[] data = requestData.getData();
		
		
		if (method.equalsIgnoreCase(Method.GET.value())) {
			return doGet(url, headers);
		} else if (method.equalsIgnoreCase(Method.POST.value())) {
			return doPost(url, headers, data);
		} else {
			throw new UnsupportedMethodTypeException(method);
		}
	}


	/**
	 * 获取指定页面的文本内容。
	 * 
	 * @param method
	 * @param httpParams
	 * @param url
	 * @param headers
	 * @param data
	 * @return
	 * @throws IOException 
	 * @throws UnsupportedOperationException 
	 * @throws UnsupportedMethodTypeException 
	 */
	private static ResponseData request(String method, MapValue httpParams, String url, MapValue headers, byte[] data) throws UnsupportedOperationException, IOException, UnsupportedMethodTypeException {

		CloseableHttpClient client = HttpClients.createDefault();

		try {
			Date beginDate = new Date();
			ResponseData responseData = new ResponseData();
			Header[] responseHeaders = null;
			byte[] responseDataByte;


			url = url.replaceAll(" ", "%20");
			
			
			Logger.log("[" + method + "] " + url);


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
				HttpPost post = new HttpPost(url);
				if (data != null) {
					// 设置 POST 字节数据。
					HttpEntity bodyData = new ByteArrayEntity(data, ContentType.APPLICATION_OCTET_STREAM);
					post.setEntity(bodyData);
				}

				request = post;
			} else {
				throw new UnsupportedMethodTypeException(method);
			}
			

			// 设置 Header 信息。
			if (headers == null) {
				headers = getDefaultRequestHeaders();
			}
			Iterator<String> headerNames = headers.keySet().iterator();
			while (headerNames.hasNext()) {
				String name = headerNames.next();
				String value = headers.getString(name);

				if (!StringUtil.isEmpty(value)) {
					if (name.equalsIgnoreCase("host")) {
						// 更正 host 参数为目标地址。
						URL u = new URL(url);
						int port = u.getPort();

						value = u.getHost();
						if (port > 1) {
							value += ":" + u.getPort();
						}
					}

					request.addHeader(name, value);
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
			
			
			Date endDate = new Date();
			// 写日志。
			log(method, httpParams, url, headers, data, responseData, beginDate, endDate);

			
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
	
	
	/**
	 * 代理请求成功后写日志。
	 * 
	 * @param method
	 * @param httpParams
	 * @param url
	 * @param headers
	 * @param data
	 * @param responseData
	 * @param beginDate
	 * @param endDate
	 */
	private static void log(final String method, final MapValue httpParams, final String url,
			final MapValue headers, final byte[] data, final ResponseData responseData, final Date beginDate, Date endDate) {
		
		new Thread(new Runnable() {
			public void run() {
				try {
					final String SINGLE_LINE = "\r\n";
					final String LINE = "\r\n\r\n";
					final String BIG_LINE = LINE + LINE;

					Date now = new Date();
					String monthPath = new SimpleDateFormat("yyyyMM").format(now);
					String dayPath = new SimpleDateFormat("dd").format(now);
					String path = SystemConfig.getSystemPath() + "history/" + monthPath + "/" + dayPath + "/";
					String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(now) ;
					int random = NumberUtil.getRandom(1000, 9999);
					String filename = "[" + timestamp + "-" + random + "]" + URLEncoder.encode(url, StringUtil.UTF8);
					String ext = "txt";
					File file = new File(path + filename + "." + ext);

					
					SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					
					StringBuffer buffer = new StringBuffer();
					buffer.append("==== REQUEST INFO ====").append(SINGLE_LINE);
					buffer.append("BEGIN： ").append(dateFormater.format(beginDate)).append(LINE);
					buffer.append("URL： ").append(url).append(LINE);
					buffer.append("METHOD： ").append(method).append(LINE);
					buffer.append("HTTP_PARAMS： ").append(httpParams).append(LINE);
					buffer.append("HEADERS： ").append(headers).append(LINE);
					buffer.append("DATA： ").append(data != null && data.length > 0 ? new String(data) : null).append(BIG_LINE);

					buffer.append("==== RESPONSE INFO ====").append(SINGLE_LINE);
					buffer.append("END： ").append(dateFormater.format(beginDate)).append(LINE);
					buffer.append("STATUS： ").append(responseData.getStatus() + "").append(LINE);
					buffer.append("HEADERS： ").append(responseData.getHeaders()).append(LINE);
					buffer.append("DATA： ").append(new String(responseData.getData()));
					
					
					FileUtil.save(file, buffer.toString());
				} catch (Exception e) {
					Logger.printStackTrace(e);
				}
			}
		}).start();
	}
}
