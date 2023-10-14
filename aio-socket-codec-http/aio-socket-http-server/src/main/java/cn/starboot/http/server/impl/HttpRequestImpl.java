/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.http.server.impl;

import cn.starboot.http.common.enums.HeaderNameEnum;
import cn.starboot.http.common.enums.HeaderValueEnum;
import cn.starboot.http.common.io.ChunkedInputStream;
import cn.starboot.http.common.io.PostInputStream;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class HttpRequestImpl extends AbstractRequest {

	/**
	 * 释放维持长连接
	 */
	private boolean keepAlive;
	/**
	 * 空流
	 */
	protected static final InputStream EMPTY_INPUT_STREAM = new InputStream() {
		@Override
		public int read() {
			return -1;
		}
	};

    private final HttpResponseImpl response;
    private InputStream inputStream;

    HttpRequestImpl(HttpRequestPacket HTTPRequestPacket) {
        init(HTTPRequestPacket);
        this.response = new HttpResponseImpl(this, HTTPRequestPacket);
    }

    public final HttpResponseImpl getResponse() {
        return response;
    }

	public boolean isKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

    @Override
    public InputStream getInputStream() throws IOException {
		if (inputStream != null) {
			return inputStream;
		}

		//如果一个消息即存在传输译码（Transfer-Encoding）头域并且也 Content-Length 头域，后者会被忽略。
		if (HeaderValueEnum.CHUNKED.getName().equalsIgnoreCase(HTTPRequestPacket.getHeader(HeaderNameEnum.TRANSFER_ENCODING.getName()))) {
			inputStream = new ChunkedInputStream(HTTPRequestPacket.getAioChannelContext());
		} else {
			int contentLength = getContentLength();
			if (contentLength > 0 && HTTPRequestPacket.getFormUrlencoded() == null) {
				inputStream = new PostInputStream(HTTPRequestPacket.getAioChannelContext().getInputStream(contentLength), contentLength);
			} else {
				inputStream = EMPTY_INPUT_STREAM;
			}
		}
		return inputStream;
    }

    public void reset() {
        HTTPRequestPacket.reset();
        response.reset();
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = null;
        }
    }



}
