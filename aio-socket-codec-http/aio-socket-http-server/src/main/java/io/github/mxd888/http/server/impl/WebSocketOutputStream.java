package io.github.mxd888.http.server.impl;

import io.github.mxd888.http.common.enums.HeaderNameEnum;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
final class WebSocketOutputStream extends AbstractOutputStream {

    public WebSocketOutputStream(WebSocketRequestImpl webSocketRequest, WebSocketResponseImpl response, HttpRequestPacket HTTPRequestPacket) {
        super(webSocketRequest, response, HTTPRequestPacket);
        super.chunked = false;
    }

    protected byte[] getHeadPart(boolean hasHeader) {
        return getBytes(request.getProtocol() + " " + response.getHttpStatus() + " " + response.getReasonPhrase() + "\r\n"
                + HeaderNameEnum.CONTENT_TYPE.getName() + ":" + response.getContentType() + (hasHeader ? "\r\n" : "\r\n\r\n"));
    }

}
