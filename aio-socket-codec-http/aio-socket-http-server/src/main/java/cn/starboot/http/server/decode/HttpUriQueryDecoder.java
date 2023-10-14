/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.http.server.decode;

import cn.starboot.http.common.utils.Constant;
import cn.starboot.http.common.utils.StringUtils;
import cn.starboot.http.server.HttpServerConfiguration;
import cn.starboot.http.server.impl.HttpRequestPacket;
import cn.starboot.socket.core.ChannelContext;

import java.nio.ByteBuffer;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
class HttpUriQueryDecoder extends AbstractDecoder {

    private final HttpProtocolDecoder decoder = new HttpProtocolDecoder(getConfiguration());

    public HttpUriQueryDecoder(HttpServerConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Decoder decode(ByteBuffer byteBuffer, ChannelContext channelContext, HttpRequestPacket HTTPRequestPacket) {
        int length = scanUriQuery(byteBuffer);
        if (length >= 0) {
            String query = StringUtils.convertToString(byteBuffer, byteBuffer.position() - 1 - length, length);
            HTTPRequestPacket.setQueryString(query);
            return decoder.decode(byteBuffer, channelContext, HTTPRequestPacket);
        } else {
            return this;
        }

    }

    private int scanUriQuery(ByteBuffer buffer) {
        if (!buffer.hasRemaining()) {
            return -1;
        }
        int i = 0;
        buffer.mark();
        while (buffer.hasRemaining()) {
            if (buffer.get() == Constant.SP) {
                return i;
            }
            i++;
        }
        buffer.reset();
        return -1;
    }
}
