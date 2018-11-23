
package com.yy.app.webagent.server;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.StringUtil;

import com.yy.app.webagent.data.DataFactory;
import com.yy.app.webagent.data.RequestDataStruct;
import com.yy.app.webagent.server.exception.UnsupportedMethodException;
import com.yy.util.MapValue;
import com.yy.web.ServletHttp;


public class RequestHandler extends AbstractHandler {
	
	private static final String RESPONSE_CONTENT_TYPE = "application/octet-stream";
	
	private String target;
	private Request baseRequest;
	private HttpServletRequest request;
	private HttpServletResponse response;
	

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		this.target = target;
		this.baseRequest = baseRequest;
		this.request = request;
		this.response = response;
		
		byte[] bytes = IOUtils.toByteArray(request.getInputStream());
		RequestDataStruct requesetData = DataFactory.parseRequestData(bytes);

		if (requesetData != null) {
			// 默认代理。
		} else {
			String url = ServletHttp.request_S(request, "url");
			if (StringUtil.isNotBlank(url)) {
				// GET 方式代理。
				try {
					ProxyServer.doGet(url, getHeaders());
				} catch (UnsupportedOperationException e) {
					e.printStackTrace();
				} catch (UnsupportedMethodException e) {
					e.printStackTrace();
				}
			} else {
				// 解析数据失败。
				outParseDataError();
			}
			
		}
	}
	
	
	/**
	 * 获取 Request 的请求头信息。
	 * 
	 * @return
	 */
	private MapValue getHeaders() {
		
		MapValue headers = new MapValue();
		HttpServletRequest request = getRequest();
		Enumeration<String> headerNames = request.getHeaderNames();
		
		while (headerNames.hasMoreElements()) {
			String name = headerNames.nextElement();
			String value = request.getHeader(name);

			headers.put(name, value);
		}
		
		
		return headers;
	}
	
	
	/**
	 * 返回解析数据错误的结果。
	 * 
	 * @throws IOException
	 */
	private void outParseDataError() throws IOException {
		
		HttpServletResponse response = getResponse();

		response.setContentType(RESPONSE_CONTENT_TYPE);
		response.setStatus(HttpServletResponse.SC_OK);
		getBaseRequest().setHandled(true);
		response.getWriter().println("parse data error");
	}


	public String getTarget() {
		return target;
	}


	public Request getBaseRequest() {
		return baseRequest;
	}


	public HttpServletRequest getRequest() {
		return request;
	}


	public HttpServletResponse getResponse() {
		return response;
	}
}