package cn.starboot.http.server.decode;

import cn.starboot.http.common.utils.Constant;
import cn.starboot.http.server.impl.HttpRequestHandler;
import cn.starboot.http.server.impl.HttpRequestPacket;
import cn.starboot.socket.core.ChannelContext;

import java.nio.ByteBuffer;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class IgnoreHeaderDecoder implements Decoder {

    @Override
    public Decoder decode(ByteBuffer byteBuffer, ChannelContext channelContext, HttpRequestPacket httpHeader) {
        int position = byteBuffer.position() + byteBuffer.arrayOffset();
        int limit = byteBuffer.limit() + byteBuffer.arrayOffset();
        byte[] data = byteBuffer.array();

        while (limit - position >= 4) {
            byte b = data[position + 3];
            if (b == Constant.CR) {
                position++;
                continue;
            } else if (b != Constant.LF) {
                position += 7;
                if (position >= limit || (data[position] == Constant.CR || data[position] == Constant.LF)) {
                    position -= 3;
                }
                continue;
            }
            // header 结束符匹配，最后2字节已经是CR、LF,无需重复验证
            if (data[position] == Constant.CR && data[position + 1] == Constant.LF) {
                byteBuffer.position(position + 4 - byteBuffer.arrayOffset());
                return HttpRequestHandler.BODY_READY_DECODER;
            } else {
                position += 2;
            }
        }
        byteBuffer.position(position - byteBuffer.arrayOffset());
        return this;
    }
}
