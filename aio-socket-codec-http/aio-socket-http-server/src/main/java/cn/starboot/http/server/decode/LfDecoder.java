package cn.starboot.http.server.decode;

import cn.starboot.http.common.enums.HttpStatus;
import cn.starboot.http.common.exception.HttpException;
import cn.starboot.http.common.utils.Constant;
import cn.starboot.http.server.HttpServerConfiguration;
import cn.starboot.http.server.impl.HttpRequestPacket;
import cn.starboot.socket.core.ChannelContext;

import java.nio.ByteBuffer;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
class LfDecoder extends AbstractDecoder {
    private final AbstractDecoder nextDecoder;

    public LfDecoder(AbstractDecoder nextDecoder, HttpServerConfiguration configuration) {
        super(configuration);
        this.nextDecoder = nextDecoder;
    }

    @Override
    public Decoder decode(ByteBuffer byteBuffer, ChannelContext channelContext, HttpRequestPacket HTTPRequestPacket) {
        if (byteBuffer.hasRemaining()) {
            if (byteBuffer.get() != Constant.LF) {
                throw new HttpException(HttpStatus.BAD_REQUEST);
            }
            return nextDecoder.decode(byteBuffer, channelContext, HTTPRequestPacket);
        }
        return this;
    }
}
