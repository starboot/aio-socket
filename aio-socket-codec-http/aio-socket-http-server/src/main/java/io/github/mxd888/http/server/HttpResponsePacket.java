package io.github.mxd888.http.server;

import cn.starboot.socket.Packet;

/**
 * 对外输出消息体，将其编码进输出流
 */
public class HttpResponsePacket extends Packet {

	private HttpResponse response;

	private HttpRequest request;

	private byte[] data;

	public HttpResponsePacket(HttpResponse response, HttpRequest request) {
		this.response = response;
		this.request = request;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public HttpResponse getResponse() {
		return response;
	}

	public void setResponse(HttpResponse response) {
		this.response = response;
	}

	public HttpRequest getRequest() {
		return request;
	}

	public void setRequest(HttpRequest request) {
		this.request = request;
	}
}
