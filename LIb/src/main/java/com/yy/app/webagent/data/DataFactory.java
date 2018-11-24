package com.yy.app.webagent.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.alibaba.fastjson.JSON;
import com.yy.log.Logger;
import com.yy.util.MapValue;
import com.yy.util.NumberUtil;

/**
 * 数据结构工厂。
 * 
 * @since  2018-11-23
 * @author Admin
 *
 */
public class DataFactory {

	/** 请求头字节大小。 */
	private static final int HEADER_SIZE = 63;

	
	/**
	 * 构建请求数据。
	 * 
	 * @param url
	 * @param method
	 * @param headers
	 * @param data
	 * @return
	 */
	public static byte[] buildRequestData(String url, String method, MapValue headers, byte[] data) {
		
		if (headers == null) {
			headers = new MapValue();
		}
		
		RequestDataStruct.ParamsStruct params = new RequestDataStruct.ParamsStruct();
		params.setUrl(url);
		params.setMethod(method);
		params.setHeaders(headers);

				
		return buildData(params, data);
	}

	
	/**
	 * 构造响应数据。
	 * 
	 * @param headers
	 * @param data
	 * @return
	 */
	public static byte[] buildResponseData(MapValue headers, byte[] data) {
		
		if (headers == null) {
			headers = new MapValue();
		}
		
		
		ResponseDataStruct.ParamsStruct params = new ResponseDataStruct.ParamsStruct();
		params.setHeaders(headers);

				
		return buildData(params, data);
	}
	
	
	/**
	 * 构建字节数据。
	 * 
	 * @param params
	 * @param data
	 * @return
	 */
	private static byte[] buildData(Object params, byte[] data) {
		
		// 参数行。
		byte[] paramsByte = JSON.toJSONBytes(params);
		int paramsByteSize = paramsByte.length;
		byte[] paramsLengthByte = (paramsByteSize + "").getBytes();


		// 头部大小行。
		byte[] headerSizeLineByte = new byte[HEADER_SIZE];
		System.arraycopy(paramsLengthByte, 0, headerSizeLineByte, 0, paramsLengthByte.length);


		byte[] result = null;
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			out.write(headerSizeLineByte);
			out.write(paramsByte);

			if (data != null && data.length > 0) {
				out.write(data);
			}

			out.flush();

			result = out.toByteArray();
		} catch (IOException e) {
			Logger.printStackTrace(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					Logger.printStackTrace(e);
				}
			}
		}
		
		
		return result;
	}
	
	
	/**
	 * 解构请求数据。
	 * 
	 * @param bytes
	 * @return
	 */
	public static RequestDataStruct parseRequestData(byte[] bytes) {

		// 头部大小。
		byte[] headerSizeByte = new byte[HEADER_SIZE];
        System.arraycopy(bytes, 0, headerSizeByte, 0, HEADER_SIZE);
        String headerSizeStr = new String(headerSizeByte).trim();
        int headerSize = NumberUtil.parseInt(headerSizeStr);

        // 请求头。
        byte[] paramsByte = new byte[headerSize];
        System.arraycopy(bytes, HEADER_SIZE, paramsByte, 0, headerSize);
        String paramsStr = new String(paramsByte);
        RequestDataStruct.ParamsStruct params = JSON.parseObject(paramsStr, RequestDataStruct.ParamsStruct.class);
        
        // 数据。
        int dataSize = bytes.length - HEADER_SIZE - headerSize;
        byte[] data = null;
        if (dataSize > 0) {
	        data = new byte[dataSize];
	        System.arraycopy(bytes, HEADER_SIZE + headerSize, data, 0, data.length);
        }
        
        
        RequestDataStruct requestData = new RequestDataStruct();
        requestData.setParams(params);
        requestData.setData(data);
        
		
		return requestData;
	}
	
	
	/**
	 * 解构响应数据。
	 * 
	 * @param bytes
	 * @return
	 */
	public static ResponseDataStruct parseResponseData(byte[] bytes) {
		
		// 头部大小。
		byte[] headerSizeByte = new byte[HEADER_SIZE];
        System.arraycopy(bytes, 0, headerSizeByte, 0, HEADER_SIZE);
        String headerSizeStr = new String(headerSizeByte).trim();
        int headerSize = NumberUtil.parseInt(headerSizeStr);

        // 请求头。
        byte[] paramsByte = new byte[headerSize];
        System.arraycopy(bytes, HEADER_SIZE, paramsByte, 0, headerSize);
        String paramsStr = new String(paramsByte);
        ResponseDataStruct.ParamsStruct params = JSON.parseObject(paramsStr, ResponseDataStruct.ParamsStruct.class);
        
        // 数据。
        int dataSize = bytes.length - HEADER_SIZE - headerSize;
        byte[] data = null;
        if (dataSize > 0) {
	        data = new byte[dataSize];
	        System.arraycopy(bytes, HEADER_SIZE + headerSize, data, 0, data.length);
        }
        
        
        ResponseDataStruct requestData = new ResponseDataStruct();
        requestData.setParams(params);
        requestData.setData(data);
        
		
		return requestData;
	}
}
