package io.github.mxd888.http.server.decode;

import io.github.mxd888.http.common.enums.HttpStatus;
import io.github.mxd888.http.common.exception.HttpException;
import io.github.mxd888.http.common.utils.Constant;
import io.github.mxd888.http.server.HttpServerConfiguration;
import io.github.mxd888.http.server.impl.Request;
import io.github.mxd888.socket.core.ChannelContext;

import java.nio.ByteBuffer;

/**
 * @author 三刀（zhengjunweimail@163.com）
 * @version V1.0 , 2022/1/12
 */
class LfDecoder extends AbstractDecoder {
    private final AbstractDecoder nextDecoder;

    public LfDecoder(AbstractDecoder nextDecoder, HttpServerConfiguration configuration) {
        super(configuration);
        this.nextDecoder = nextDecoder;
    }

    @Override
    public Decoder decode(ByteBuffer byteBuffer, ChannelContext channelContext, Request request) {
        if (byteBuffer.hasRemaining()) {
            if (byteBuffer.get() != Constant.LF) {
                throw new HttpException(HttpStatus.BAD_REQUEST);
            }
            return nextDecoder.decode(byteBuffer, channelContext, request);
        }
        return this;
    }
}
