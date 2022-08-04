package io.github.mxd888.http.server.decode;


import io.github.mxd888.http.common.enums.HttpStatus;
import io.github.mxd888.http.common.exception.HttpException;
import io.github.mxd888.http.common.utils.ByteTree;
import io.github.mxd888.http.common.utils.Constant;
import io.github.mxd888.http.common.utils.StringUtils;
import io.github.mxd888.http.server.HttpServerConfiguration;
import io.github.mxd888.http.server.ServerHandler;
import io.github.mxd888.http.server.impl.HttpRequestHandler;
import io.github.mxd888.http.server.impl.Request;
import io.github.mxd888.socket.core.ChannelContext;

import java.nio.ByteBuffer;
import java.util.function.Function;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
class HttpHeaderDecoder extends AbstractDecoder {
    private static final ByteTree.EndMatcher COLON_END_MATCHER = endByte -> endByte == Constant.COLON;

    private final HeaderValueDecoder headerValueDecoder = new HeaderValueDecoder();
    private final IgnoreHeaderDecoder ignoreHeaderDecoder = new IgnoreHeaderDecoder();
    private final LfDecoder lfDecoder = new LfDecoder(this, getConfiguration());

    public HttpHeaderDecoder(HttpServerConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Decoder decode(ByteBuffer byteBuffer, ChannelContext channelContext, Request request) {
        if (request.getHeaderSize() >= 0 && request.getHeaderSize() >= getConfiguration().getHeaderLimiter()) {
            return ignoreHeaderDecoder.decode(byteBuffer, channelContext, request);
        }
        if (byteBuffer.remaining() < 2) {
            return this;
        }
        //header解码结束
        if (byteBuffer.get(byteBuffer.position()) == Constant.CR) {
            if (byteBuffer.get(byteBuffer.position() + 1) != Constant.LF) {
                throw new HttpException(HttpStatus.BAD_REQUEST);
            }
            byteBuffer.position(byteBuffer.position() + 2);
//            return decoder.decode(byteBuffer, aioSession, request);
            return HttpRequestHandler.BODY_READY_DECODER;
        }
        //Header name解码
        ByteTree<Function<String, ServerHandler<?, ?>>> name = StringUtils.scanByteTree(byteBuffer, COLON_END_MATCHER, getConfiguration().getHeaderNameByteTree());
        if (name == null) {
            return this;
        }
//        System.out.println("headerName: " + name);
        request.setHeaderTemp(name);
        return headerValueDecoder.decode(byteBuffer, channelContext, request);
    }

    /**
     * Value值解码
     */
    class HeaderValueDecoder implements Decoder {
        @Override
        public Decoder decode(ByteBuffer byteBuffer, ChannelContext channelContext, Request request) {
            ByteTree<?> value = StringUtils.scanByteTree(byteBuffer, CR_END_MATCHER, getConfiguration().getByteCache());
            if (value == null) {
                if (byteBuffer.remaining() == byteBuffer.capacity()) {
                    throw new HttpException(HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE);
                }
                return this;
            }
//            System.out.println("value: " + value);
            request.setHeadValue(value.getStringValue());
            return lfDecoder.decode(byteBuffer, channelContext, request);
        }
    }
}
