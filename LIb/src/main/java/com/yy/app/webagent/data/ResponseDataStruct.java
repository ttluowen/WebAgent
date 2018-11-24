package com.yy.app.webagent.data;

import com.yy.util.MapValue;

public class ResponseDataStruct {

	private ParamsStruct params;
	private byte[] data;

	public ParamsStruct getParams() {
		return params;
	}

	public void setParams(ParamsStruct params) {
		this.params = params;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public static class ParamsStruct {

		private MapValue headers;

		public MapValue getHeaders() {
			return headers;
		}

		public void setHeaders(MapValue headers) {
			this.headers = headers;
		}
	}
}
