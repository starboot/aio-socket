package io.github.mxd888.http.server.decode;

import io.github.mxd888.http.common.utils.Constant;
import io.github.mxd888.http.server.impl.HttpRequestHandler;
import io.github.mxd888.http.server.impl.Request;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.core.TCPChannelContext;

import java.nio.ByteBuffer;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class IgnoreHeaderDecoder implements Decoder {

    @Override
    public Decoder decode(ByteBuffer byteBuffer, ChannelContext channelContext, Request httpHeader) {
        int position = byteBuffer.position() + byteBuffer.arrayOffset();
        int limit = byteBuffer.limit() + byteBuffer.arrayOffset();
        byte[] data = byteBuffer.array();

        while (limit - position >= 4) {
            byte b = data[position + 3];
            if (b > Constant.CR || (b != Constant.CR && b != Constant.LF)) {
                position += 4;
                continue;
            }
            int index = 0;
            // header 结束符匹配
            while (index < Constant.HEADER_END.length) {
                if (data[position++] != Constant.HEADER_END[index]) {
                    break;
                }
                index++;
            }
            if (index == Constant.HEADER_END.length) {
                byteBuffer.position(position - byteBuffer.arrayOffset());
                return HttpRequestHandler.BODY_READY_DECODER;
            }
        }
        byteBuffer.position(position - byteBuffer.arrayOffset());
        return this;
    }
}
