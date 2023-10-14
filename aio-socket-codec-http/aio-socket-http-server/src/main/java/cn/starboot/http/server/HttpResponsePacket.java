/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.http.server;

import cn.starboot.socket.Packet;

/**
 * 对外输出消息体，将其编码进输出流
 */
public class HttpResponsePacket extends Packet {

	/* uid */
	private static final long serialVersionUID = 8933782815134212600L;

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
