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

import cn.starboot.http.server.HttpRequest;
import cn.starboot.http.server.HttpResponse;
import cn.starboot.http.server.HttpServerConfiguration;
import cn.starboot.socket.core.Aio;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.http.common.Reset;
import cn.starboot.http.common.enums.HeaderNameEnum;
import cn.starboot.http.common.enums.HttpMethodEnum;
import cn.starboot.http.common.enums.HttpProtocolEnum;
import cn.starboot.http.common.enums.HttpStatus;
import cn.starboot.http.common.utils.Constant;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public abstract class AbstractOutputStream extends OutputStream implements Reset {

	protected boolean committed = false;
	protected boolean chunked = false;
	protected boolean gzip = false;
    protected static String SERVER_LINE = null;
    protected final AbstractResponse response;
    protected final HttpRequest request;
    protected final HttpServerConfiguration configuration;
	private final HttpResponsePacket httpResponsePacket;
	private final ChannelContext context;
	/**
	 * 当前流是否完结
	 */
	private boolean closed = false;


    public AbstractOutputStream(HttpRequest httpRequest, AbstractResponse response, HttpRequestPacket httpRequestPacket) {
        this.response = response;
        this.request = httpRequest;
        this.configuration = httpRequestPacket.getConfiguration();
		context = httpRequestPacket.getAioChannelContext();
        if (SERVER_LINE == null) {
            SERVER_LINE = HeaderNameEnum.SERVER.getName() + Constant.COLON_CHAR + configuration.serverName() + Constant.CRLF;
        }
        this.httpResponsePacket = new HttpResponsePacket(this.response);
    }

	@Override
	public final void write(int b) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void write(byte[] b, int off, int len) throws IOException {
		check();
		httpResponsePacket.setClosed(false);
		httpResponsePacket.setData(b);
		flush();
	}

	public final void directWrite(byte[] b, int off, int len) throws IOException {
		httpResponsePacket.setDirectWrite(true);
		write(b, off, len);
	}

	public final void write(ByteBuffer buffer) throws IOException {
    	write(buffer.array());
	}

	@Override
	public final void flush() throws IOException {
		writeHeader();
		// send
		Aio.bSend(this.context, this.httpResponsePacket);
	}

	public final boolean isClosed() {
		return closed;
	}

	public final void reset() {
		committed = closed = chunked = gzip = false;
	}


    protected abstract byte[] getHeadPart(boolean hasHeader);

	protected boolean hasHeader(HttpResponse response) {
		return response.getHeaders().size() > 0;
	}

    @Override
    public void close() throws IOException {
        //识别是否采用 chunked 输出
		check();
		if (closed) {
			throw new IOException("outputStream has already closed");
		}
		httpResponsePacket.setClosed(true);
		httpResponsePacket.setChunked(chunked);
		closed = true;
		// 在这里输出一下
		flush();
    }

    protected final void check() {
        //识别是否采用 chunked 输出
        if (!committed) {
            chunked = supportChunked(request, response);
        }
    }

	protected void writeHeader() {
		if (committed) {
			return;
		}
		httpResponsePacket.setChunked(chunked);
		// 初始化头部
		boolean hasHeader = hasHeader(response);
		byte[] headPart = getHeadPart(hasHeader);
		httpResponsePacket.setHasHeader(hasHeader);
		httpResponsePacket.setHeadPart(headPart);
		committed = true;
	}

	protected final byte[] getBytes(String str) {
		return str.getBytes(StandardCharsets.US_ASCII);
	}

    /**
     * 是否支持chunked输出
     *
     * @return .
     */
    private boolean supportChunked(HttpRequest request, AbstractResponse response) {
        //gzip采用chunked编码
        gzip = response.isGzip();
        if (gzip) {
            response.setContentLength(-1);
            return true;
        }
        return response.getContentLength() < 0
                && (request.getMethod().equals(HttpMethodEnum.GET.getMethod())
                || request.getMethod().equals(HttpMethodEnum.POST.getMethod())
                || request.getMethod().equals(HttpMethodEnum.PUT.getMethod()))
                && response.getHttpStatus() != HttpStatus.CONTINUE.value()
                && HttpProtocolEnum.HTTP_11.getProtocol().equals(request.getProtocol());
    }
}
