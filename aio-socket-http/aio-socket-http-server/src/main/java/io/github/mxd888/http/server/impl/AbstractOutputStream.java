package io.github.mxd888.http.server.impl;

import io.github.mxd888.http.common.BufferOutputStream;
import io.github.mxd888.http.common.Cookie;
import io.github.mxd888.http.common.HeaderValue;
import io.github.mxd888.http.common.enums.HeaderNameEnum;
import io.github.mxd888.http.common.enums.HttpMethodEnum;
import io.github.mxd888.http.common.enums.HttpProtocolEnum;
import io.github.mxd888.http.common.enums.HttpStatus;
import io.github.mxd888.http.common.utils.Constant;
import io.github.mxd888.http.server.HttpRequest;
import io.github.mxd888.http.server.HttpServerConfiguration;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author 三刀
 * @version V1.0 , 2018/2/3
 */
abstract class AbstractOutputStream extends BufferOutputStream {


    protected static String SERVER_LINE = null;
    protected final AbstractResponse response;
    protected final HttpRequest request;
    protected final HttpServerConfiguration configuration;

    public AbstractOutputStream(HttpRequest httpRequest, AbstractResponse response, Request request) {
        super(request.getAioChannelContext());
        this.response = response;
        this.request = httpRequest;
        this.configuration = request.getConfiguration();
        if (SERVER_LINE == null) {
            SERVER_LINE = HeaderNameEnum.SERVER.getName() + Constant.COLON_CHAR + configuration.serverName() + Constant.CRLF;
        }
    }

    /**
     * 输出Http消息头
     */
    protected void writeHeader() throws IOException {
        if (committed) {
            return;
        }
        //转换Cookie
        convertCookieToHeader();

        boolean hasHeader = hasHeader();
        //输出http状态行、contentType,contentLength、Transfer-Encoding、server等信息
        virtualBuffer = channelContext.getByteBuf();
        ByteBuffer buffer = virtualBuffer.buffer();
        buffer.put(getHeadPart(hasHeader));
        if (hasHeader) {
            //输出Header部分
            writeHeaders();
        }
        committed = true;
    }

    protected abstract byte[] getHeadPart(boolean hasHeader);

    private void convertCookieToHeader() {
        List<Cookie> cookies = response.getCookies();
        if (cookies.size() > 0) {
            cookies.forEach(cookie -> response.addHeader(HeaderNameEnum.SET_COOKIE.getName(), cookie.toString()));
        }
    }

    protected boolean hasHeader() {
        return response.getHeaders().size() > 0;
    }

    private void writeHeaders() throws IOException {
        ByteBuffer buffer = virtualBuffer.buffer();
        for (Map.Entry<String, HeaderValue> entry : response.getHeaders().entrySet()) {
            HeaderValue headerValue = entry.getValue();
            while (headerValue != null) {
                buffer.put(getHeaderNameBytes(entry.getKey()));
                buffer.put(getBytes(headerValue.getValue()));
                buffer.put(Constant.CRLF_BYTES);
                headerValue = headerValue.getNextValue();
            }
        }
        buffer.put(Constant.CRLF_BYTES);
    }

    @Override
    public void close() throws IOException {
        //识别是否采用 chunked 输出
        if (!committed) {
            chunked = supportChunked(request, response);
        }
        super.close();
    }

    @Override
    protected final void check() {
        //识别是否采用 chunked 输出
        if (!committed) {
            chunked = supportChunked(request, response);
        }
    }

    /**
     * 是否支持chunked输出
     *
     * @return
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
