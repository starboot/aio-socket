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
import cn.starboot.http.common.Reset;
import cn.starboot.http.server.HttpRequest;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
abstract class AbstractRequest implements HttpRequest, Reset {

    protected HttpRequestPacket HTTPRequestPacket;

    protected void init(HttpRequestPacket HTTPRequestPacket) {
        this.HTTPRequestPacket = HTTPRequestPacket;
    }


    @Override
    public final String getHeader(String headName) {
        return HTTPRequestPacket.getHeader(headName);
    }

    @Override
    public final Collection<String> getHeaders(String name) {
        return HTTPRequestPacket.getHeaders(name);
    }

    @Override
    public final Collection<String> getHeaderNames() {
        return HTTPRequestPacket.getHeaderNames();
    }


    @Override
    public final String getRequestURI() {
        return HTTPRequestPacket.getRequestURI();
    }

    @Override
    public final String getProtocol() {
        return HTTPRequestPacket.getProtocol();
    }

    @Override
    public final String getMethod() {
        return HTTPRequestPacket.getMethod();
    }

    @Override
    public final String getScheme() {
        return HTTPRequestPacket.getScheme();
    }

    @Override
    public final String getRequestURL() {
        return HTTPRequestPacket.getRequestURL();
    }

    @Override
    public final String getQueryString() {
        return HTTPRequestPacket.getQueryString();
    }

    @Override
    public final String getContentType() {
        return HTTPRequestPacket.getContentType();
    }

    @Override
    public final int getContentLength() {
        return HTTPRequestPacket.getContentLength();
    }

    @Override
    public final String getParameter(String name) {
        return HTTPRequestPacket.getParameter(name);
    }

    @Override
    public final Map<String, String[]> getParameters() {
        return HTTPRequestPacket.getParameters();
    }

    @Override
    public final String[] getParameterValues(String name) {
        return HTTPRequestPacket.getParameters().get(name);
    }

    @Override
    public final String getRemoteAddr() {
        return HTTPRequestPacket.getRemoteAddr();
    }

    @Override
    public final InetSocketAddress getRemoteAddress() {
        return HTTPRequestPacket.getRemoteAddress();
    }

    @Override
    public final InetSocketAddress getLocalAddress() {
        return HTTPRequestPacket.getLocalAddress();
    }

    @Override
    public final String getRemoteHost() {
        return HTTPRequestPacket.getRemoteHost();
    }

    @Override
    public final Locale getLocale() {
        return HTTPRequestPacket.getLocale();
    }

    @Override
    public final Enumeration<Locale> getLocales() {
        return HTTPRequestPacket.getLocales();
    }

    @Override
    public final String getCharacterEncoding() {
        return HTTPRequestPacket.getCharacterEncoding();
    }

    public final HttpRequestPacket getHTTPRequestPacket() {
        return HTTPRequestPacket;
    }

    @Override
    public Cookie[] getCookies() {
        return HTTPRequestPacket.getCookies();
    }

    @Override
    public <A> A getAttachment() {
        return HTTPRequestPacket.getAttachment();
    }

    @Override
    public <A> void setAttachment(A attachment) {
        HTTPRequestPacket.setAttachment(attachment);
    }

    public abstract AbstractResponse getResponse();
}