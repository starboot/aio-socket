package io.github.mxd888.http.server.decode;

import io.github.mxd888.http.common.utils.Constant;
import io.github.mxd888.http.common.utils.StringUtils;
import io.github.mxd888.http.server.HttpServerConfiguration;
import io.github.mxd888.http.server.impl.Request;
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
    public Decoder decode(ByteBuffer byteBuffer, ChannelContext channelContext, Request request) {
        int length = scanUriQuery(byteBuffer);
        if (length >= 0) {
            String query = StringUtils.convertToString(byteBuffer, byteBuffer.position() - 1 - length, length);
            request.setQueryString(query);
            return decoder.decode(byteBuffer, channelContext, request);
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
