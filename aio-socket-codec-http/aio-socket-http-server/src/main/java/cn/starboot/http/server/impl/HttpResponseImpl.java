package cn.starboot.http.server.impl;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
class HttpResponseImpl extends AbstractResponse {

    public HttpResponseImpl(HttpRequestImpl httpRequest, HttpRequestPacket HTTPRequestPacket) {
        init(httpRequest, new HttpOutputStream(httpRequest, this, HTTPRequestPacket));
    }
}
