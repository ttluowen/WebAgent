package com.yy.app.webagent.server;

import com.yy.util.MapValue;

public class ResponseData {

	private int status;
	private MapValue headers;
	private byte[] data;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public MapValue getHeaders() {
		return headers;
	}

	public void setHeaders(MapValue headers) {
		this.headers = headers;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

}
