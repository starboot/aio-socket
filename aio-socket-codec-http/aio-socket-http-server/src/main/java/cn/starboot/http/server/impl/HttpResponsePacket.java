/*
 *    Copyright 2019 The aio-socket Project
 *
 *    The aio-socket Project Licenses this file to you under the Apache License,
 *    Version 2.0 (the "License"); you may not use this file except in compliance
 *    with the License. You may obtain a copy of the License at:
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package cn.starboot.http.server.impl;

import cn.starboot.socket.Packet;
import cn.starboot.http.server.HttpResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by DELL(mxd) on 2022/12/28 13:44
 */
public class HttpResponsePacket extends Packet {

	private static final Map<String, byte[]> HEADER_NAME_EXT_MAP = new ConcurrentHashMap<>();

	private final HttpResponse response;

	private boolean gzip = false;

	private boolean chunked = false;

	private boolean directWrite = false;

	private boolean closed = false;

	private boolean hasHeader = false;

	private byte[] headPart;

	private boolean committed = false;

	/*
	http 报文
	 */
	private byte[] data;

	public HttpResponsePacket(HttpResponse response) {
		this.response = response;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public boolean isGzip() {
		return gzip;
	}

	public void setGzip(boolean gzip) {
		this.gzip = gzip;
	}

	public boolean isChunked() {
		return chunked;
	}

	public void setChunked(boolean chunked) {
		this.chunked = chunked;
	}

	public boolean isDirectWrite() {
		return directWrite;
	}

	public void setDirectWrite(boolean directWrite) {
		this.directWrite = directWrite;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public HttpResponse getResponse() {
		return response;
	}

	public boolean isHasHeader() {
		return hasHeader;
	}

	public void setHasHeader(boolean hasHeader) {
		this.hasHeader = hasHeader;
	}

	public byte[] getHeadPart() {
		return headPart;
	}

	public void setHeadPart(byte[] headPart) {
		this.headPart = headPart;
	}

	public boolean isCommitted() {
		return committed;
	}

	public void setCommitted(boolean committed) {
		this.committed = committed;
	}
}
