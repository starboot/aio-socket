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
