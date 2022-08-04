package io.github.mxd888.http.server.decode;

import io.github.mxd888.http.common.enums.HttpStatus;
import io.github.mxd888.http.common.exception.HttpException;
import io.github.mxd888.http.common.utils.ByteTree;
import io.github.mxd888.http.common.utils.Constant;
import io.github.mxd888.http.common.utils.StringUtils;
import io.github.mxd888.http.server.HttpServerConfiguration;
import io.github.mxd888.http.server.ServerHandler;
import io.github.mxd888.http.server.impl.Request;
import io.github.mxd888.socket.core.ChannelContext;

import java.nio.ByteBuffer;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
class HttpUriDecoder extends AbstractDecoder {
    private static final ByteTree.EndMatcher URI_END_MATCHER = endByte -> endByte <= '?' && (endByte == ' ' || endByte == '?');
    private final HttpUriQueryDecoder uriQueryDecoder = new HttpUriQueryDecoder(getConfiguration());
    private final HttpProtocolDecoder protocolDecoder = new HttpProtocolDecoder(getConfiguration());

    public HttpUriDecoder(HttpServerConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Decoder decode(ByteBuffer byteBuffer, ChannelContext channelContext, Request request) {
        ByteTree<ServerHandler<?, ?>> uriTreeNode = StringUtils.scanByteTree(byteBuffer, URI_END_MATCHER, getConfiguration().getUriByteTree());
        if (uriTreeNode != null) {
            request.setUri(uriTreeNode.getStringValue());
            if (uriTreeNode.getAttach() == null) {
                request.setServerHandler(request.getConfiguration().getHttpServerHandler());
            } else {
                request.setServerHandler(uriTreeNode.getAttach());
            }

            switch (byteBuffer.get(byteBuffer.position() - 1)) {
                case Constant.SP:
                    return protocolDecoder.decode(byteBuffer, channelContext, request);
                case '?':
                    return uriQueryDecoder.decode(byteBuffer, channelContext, request);
                default:
                    throw new HttpException(HttpStatus.BAD_REQUEST);
            }
        } else {
            return this;
        }
    }
}
