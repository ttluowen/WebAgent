
package com.yy.app.webagent.server;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.yy.app.webagent.data.DataFactory;
import com.yy.app.webagent.server.exception.ParseDataErrorException;
import com.yy.app.webagent.server.exception.UnsupportedMethodTypeException;
import com.yy.log.Logger;
import com.yy.util.MapValue;
import com.yy.util.StringUtil;
import com.yy.web.ServletHttp;


public class RequestHandler extends AbstractHandler {
	
	private static final String RESPONSE_BTYE_CONTENT_TYPE = "application/octet-stream";
	private static final String RESPONSE_HTML_CONTENT_TYPE = "text/html";
	
	private String target;
	private Request baseRequest;
	private HttpServletRequest request;
	private HttpServletResponse response;
	

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		this.baseRequest = baseRequest;
		this.target = target;
		this.request = request;
		this.response = response;

		try {
			byte[] bytes = IOUtils.toByteArray(request.getInputStream());
			if (bytes != null && bytes.length > 0) {
				// 默认代理。
				out(ProxyFactory.request(DataFactory.parseRequestData(bytes)));
			} else {
				String url = ServletHttp.request_S(request, "url");
				if (!StringUtil.isEmpty(url)) {
					// GET 方式代理。
					out(ProxyFactory.doGet(url, getHeaders()));
				} else {
					Logger.log(target + " 无效的请求参数");

					// 解析数据失败。
					outParseDataError();
				}
			}
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			outParseDataError();
		} catch (ParseDataErrorException e) {
			Logger.printStackTrace(e);
			outParseDataError();
		} catch (UnsupportedMethodTypeException e) {
			Logger.printStackTrace(e);
			outParseDataError();
		} catch (Exception e) {
			Logger.printStackTrace(e);
			outParseDataError();
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

		response.setContentType(RESPONSE_HTML_CONTENT_TYPE);
		response.setCharacterEncoding(StringUtil.UTF8);
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println("parse data error");
		
		getBaseRequest().setHandled(true);
	}
	
	
	/**
	 * 输出默认的字节内容。
	 * 
	 * @param responseData
	 * @throws IOException
	 */
	private void out(ResponseData responseData) throws IOException {
		
		HttpServletResponse response = getResponse();

		response.setContentType(RESPONSE_BTYE_CONTENT_TYPE);
		response.setStatus(HttpServletResponse.SC_OK);
		
		
		// 设置响应头。
		MapValue headers = responseData.getHeaders();
		if (headers != null) {
			Iterator<String> headerNames = headers.keySet().iterator();
			while (headerNames.hasNext()) {
				String name = headerNames.next();
				String value = headers.getString(name);
				if (!StringUtil.isEmpty(value)) {
					response.setHeader(name, value);
				}
			}
		}

		
		ServletOutputStream out = response.getOutputStream();
		out.write(responseData.getData());
		out.flush();
		
		getBaseRequest().setHandled(true);
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