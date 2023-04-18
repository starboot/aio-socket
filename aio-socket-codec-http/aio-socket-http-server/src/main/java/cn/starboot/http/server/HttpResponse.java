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
package cn.starboot.http.server;

import cn.starboot.http.common.Cookie;
import cn.starboot.http.common.HeaderValue;
import cn.starboot.http.common.enums.HttpStatus;
import cn.starboot.http.server.impl.AbstractOutputStream;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Http消息响应接口
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public interface HttpResponse {

    /**
     * 响应消息输出流
     *
     * @return
     */
	AbstractOutputStream getOutputStream();

    /**
     * 获取Http响应状态
     *
     * @return
     */
    int getHttpStatus();

    /**
     * 设置Http响应状态,若不设置默认{@link HttpStatus#OK}
     *
     * @param httpStatus
     */
    void setHttpStatus(HttpStatus httpStatus);

    /**
     * 设置Http响应状态,若不设置默认{@link HttpStatus#OK}
     *
     * @param httpStatus
     */
    void setHttpStatus(int httpStatus, String reasonPhrase);

	public boolean isDefaultStatus();

    /**
     * 获取Http响应描述
     *
     * @return
     */
    String getReasonPhrase();

    /**
     * Sets a response header with the given name and value. If the header had
     * already been set, the new value overwrites the previous one. The
     * <code>containsHeader</code> method can be used to test for the presence
     * of a header before setting its value.
     *
     * @param name  the name of the header
     * @param value the header value If it contains octet string, it should be
     *              encoded according to RFC 2047
     *              (http://www.ietf.org/rfc/rfc2047.txt)
     * @see #addHeader
     */
    public void setHeader(String name, String value);

    /**
     * Adds a response header with the given name and value. This method allows
     * response headers to have multiple values.
     *
     * @param name  the name of the header
     * @param value the additional header value If it contains octet string, it
     *              should be encoded according to RFC 2047
     *              (http://www.ietf.org/rfc/rfc2047.txt)
     * @see #setHeader
     */
    public void addHeader(String name, String value);

    String getHeader(String name);

	Map<String, HeaderValue> getHeaders();

	Collection<String> getHeaders(String name);

    /**
     * Get the header names set for this HTTP response.
     *
     * @return The header names set for this HTTP response.
     * @since Servlet 3.0
     */
    public Collection<String> getHeaderNames();

    void setContentLength(int contentLength);

    int  getContentLength();

    void setContentType(String contentType);

    String getContentType();

    void write(byte[] data) throws IOException;

    public String getCharacterEncoding();

    public void setCharacterEncoding(String charset);

    public void close();

    /**
     * 添加Cookie信息
     *
     * @param cookie
     */
    void addCookie(Cookie cookie);

	public List<Cookie> getCookies();
}