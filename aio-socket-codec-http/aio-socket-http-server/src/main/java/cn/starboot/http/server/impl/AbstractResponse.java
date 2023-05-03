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

import cn.starboot.http.common.Cookie;
import cn.starboot.http.common.HeaderValue;
import cn.starboot.http.common.Reset;
import cn.starboot.http.common.enums.HeaderNameEnum;
import cn.starboot.http.common.enums.HeaderValueEnum;
import cn.starboot.http.common.enums.HttpStatus;
import cn.starboot.http.server.HttpResponse;
import cn.starboot.socket.Packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
class AbstractResponse extends Packet implements HttpResponse, Reset {
	/* uid */
	private static final long serialVersionUID = 1887996841717197093L;
	/**
     * 输入流
     */
    private AbstractOutputStream outputStream;

    /**
     * 响应消息头
     */
    private Map<String, HeaderValue> headers = Collections.emptyMap();
    /**
     * http响应码
     */
    private int httpStatus = HttpStatus.OK.value();
    /**
     * 响应描述
     */
    private String reasonPhrase = HttpStatus.OK.getReasonPhrase();
    /**
     * 是否默认响应
     */
    private boolean defaultStatus = true;
    /**
     * 响应正文长度
     */
    private int contentLength = -1;

    /**
     * 正文编码方式
     */
    private String contentType = HeaderValueEnum.DEFAULT_CONTENT_TYPE.getName();

    private AbstractRequest request;

    private String characterEncoding;
    /**
     * 是否关闭Socket连接通道
     */
    private boolean closed = false;

    /**
     * 是否启用压缩模式
     */
    private boolean gzip = false;


    private List<Cookie> cookies = Collections.emptyList();

    protected void init(AbstractRequest request, AbstractOutputStream outputStream) {
        this.request = request;
        this.outputStream = outputStream;
    }


    public final void reset() {
        outputStream.reset();
        headers.clear();
        setHttpStatus(HttpStatus.OK);
        contentType = HeaderValueEnum.DEFAULT_CONTENT_TYPE.getName();
        contentLength = -1;
        characterEncoding = null;
        cookies = Collections.emptyList();
        this.closed = false;
        gzip = false;
    }


    public final AbstractOutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    public final void setHttpStatus(HttpStatus httpStatus) {
        Objects.requireNonNull(httpStatus);
        setHttpStatus(httpStatus.value(), httpStatus.getReasonPhrase());
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public final void setHttpStatus(int value, String reasonPhrase) {
        this.httpStatus = value;
        this.reasonPhrase = Objects.requireNonNull(reasonPhrase);
        defaultStatus = httpStatus == HttpStatus.OK.value() && HttpStatus.OK.getReasonPhrase().equals(reasonPhrase);
    }

    public boolean isDefaultStatus() {
        return defaultStatus;
    }

    @Override
    public final void setHeader(String name, String value) {
        setHeader(name, value, true);
    }

    @Override
    public final void addHeader(String name, String value) {
        setHeader(name, value, false);
    }

    /**
     * @param name    header name
     * @param value   header value
     * @param replace true:replace,false:append
     */
    private void setHeader(String name, String value, boolean replace) {
        char cc = name.charAt(0);
        if (cc == 'C' || cc == 'c') {
            if (checkSpecialHeader(name, value)) return;
        }
        Map<String, HeaderValue> emptyHeaders = Collections.emptyMap();
        if (headers == emptyHeaders) {
            headers = new HashMap<>();
        }
        if (replace) {
            if (value == null) {
                headers.remove(name);
            } else {
                headers.put(name, new HeaderValue(null, value));
            }
            return;
        }

        HeaderValue headerValue = headers.get(name);
        if (headerValue == null) {
            setHeader(name, value, true);
            return;
        }
        HeaderValue preHeaderValue = null;
        while (headerValue != null && !headerValue.getValue().equals(value)) {
            preHeaderValue = headerValue;
            headerValue = headerValue.getNextValue();
        }
        if (headerValue == null) {
            preHeaderValue.setNextValue(new HeaderValue(null, value));
        }
    }

    /**
     * 部分header需要特殊处理
     */
    private boolean checkSpecialHeader(String name, String value) {
        if (name.equalsIgnoreCase(HeaderNameEnum.CONTENT_TYPE.getName())) {
            setContentType(value);
            return true;
        } else if (name.equalsIgnoreCase(HeaderNameEnum.CONTENT_ENCODING.getName())) {
            gzip = HeaderValueEnum.GZIP.getName().equals(value);
        }
        return false;
    }

    @Override
    public final String getHeader(String name) {
        HeaderValue headerValue = headers.get(name);
        return headerValue == null ? null : headerValue.getValue();
    }

    public final Map<String, HeaderValue> getHeaders() {
        return headers;
    }

    @Override
    public final Collection<String> getHeaders(String name) {
        Vector<String> result = new Vector<>();
        HeaderValue headerValue = headers.get(name);
        while (headerValue != null) {
            result.addElement(headerValue.getValue());
            headerValue = headerValue.getNextValue();
        }
        return result;
    }

    @Override
    public final Collection<String> getHeaderNames() {
        return new ArrayList<>(headers.keySet());
    }

    @Override
    public final String getCharacterEncoding() {
        return characterEncoding == null ? request.getCharacterEncoding() : characterEncoding;
    }

    @Override
    public final void setCharacterEncoding(String charset) {
        this.characterEncoding = charset;
    }

    @Override
    public final void close() {
        if (closed) {
            return;
        }
        try {
            if (outputStream != null && !outputStream.isClosed()) {
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            request.getHTTPRequestPacket().getAioChannelContext().close(false);
        }
        closed = true;
    }

	@Override
    public List<Cookie> getCookies() {
        return cookies;
    }

    @Override
    public void addCookie(Cookie cookie) {
        List<Cookie> emptyList = Collections.emptyList();
        if (cookies == emptyList) {
            cookies = new ArrayList<>();
        }
        cookies.add(cookie);
    }
    @Override
    public int getContentLength() {
        return contentLength;
    }

    @Override
    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public final String getContentType() {
        return contentType;
    }

	@Override
	public void write(byte[] data) throws IOException {
    	if (data == null || data.length == 0)
    		return;
		this.write(data, 0, data.length);
	}

	public final void write(byte[] buffer, int off, int len) throws IOException {
		getOutputStream().write(buffer, off, len);
	}

	@Override
    public final void setContentType(String contentType) {
        this.contentType = Objects.requireNonNull(contentType);
    }

    /**
     * 是否要断开TCP连接
     *
     * @return true/false
     */
    public final boolean isClosed() {
        return closed;
    }

    public boolean isGzip() {
        return gzip;
    }
}
