package cn.starboot.http.server.decode;

import cn.starboot.http.common.utils.ByteTree;
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
public class HttpMethodDecoder extends AbstractDecoder {

    private final HttpUriDecoder decoder = new HttpUriDecoder(getConfiguration());

    public HttpMethodDecoder(HttpServerConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Decoder decode(ByteBuffer byteBuffer, ChannelContext channelContext, HttpRequestPacket HTTPRequestPacket) {
        ByteTree<?> method = StringUtils.scanByteTree(byteBuffer, SP_END_MATCHER, getConfiguration().getByteCache());
        if (method != null) {
            HTTPRequestPacket.setMethod(method.getStringValue());
            return decoder.decode(byteBuffer, channelContext, HTTPRequestPacket);
        } else {
            return this;
        }
    }
}
