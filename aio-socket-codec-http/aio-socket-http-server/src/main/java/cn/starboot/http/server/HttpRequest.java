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

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * Http消息请求接口
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public interface HttpRequest {

    String getHeader(String headName);

    Collection<String> getHeaders(String name);

    Collection<String> getHeaderNames();

    InputStream getInputStream() throws IOException;

    String getRequestURI();

    String getProtocol();

    String getMethod();

    String getScheme();

    String getRequestURL();

    String getQueryString();

    String getContentType();

    int getContentLength();

    String getParameter(String name);

    String[] getParameterValues(String name);

    Map<String, String[]> getParameters();

    String getRemoteAddr();

    InetSocketAddress getRemoteAddress();

    InetSocketAddress getLocalAddress();

    String getRemoteHost();

    Locale getLocale();

    Enumeration<Locale> getLocales();

    String getCharacterEncoding();

    Cookie[] getCookies();

    <A> A getAttachment();

    <A> void setAttachment(A attachment);
}
