package io.github.mxd888.http.server.impl;

import io.github.mxd888.http.common.utils.SmartDecoder;
import io.github.mxd888.http.server.decode.Decoder;



/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class RequestAttachment {
    /**
     * 请求对象
     */
    private final HttpRequestPacket HTTPRequestPacket;
    /**
     * 当前使用的解码器
     */
    private Decoder decoder;

    /**
     * Http Body解码器
     */
    private SmartDecoder bodyDecoder;

    public RequestAttachment(HttpRequestPacket HTTPRequestPacket) {
        this.HTTPRequestPacket = HTTPRequestPacket;
    }

    public HttpRequestPacket getHTTPRequestPacket() {
        return HTTPRequestPacket;
    }

    public Decoder getDecoder() {
        return decoder;
    }

    public void setDecoder(Decoder decoder) {
        this.decoder = decoder;
    }

    public SmartDecoder getBodyDecoder() {
        return bodyDecoder;
    }

    public void setBodyDecoder(SmartDecoder bodyDecoder) {
        this.bodyDecoder = bodyDecoder;
    }

}
