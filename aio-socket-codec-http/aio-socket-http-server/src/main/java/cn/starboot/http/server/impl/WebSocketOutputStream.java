/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.http.server.impl;

import cn.starboot.http.common.enums.HeaderNameEnum;

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
