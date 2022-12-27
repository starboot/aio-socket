package io.github.mxd888.http.server.decode;

import io.github.mxd888.http.server.impl.Request;
import cn.starboot.socket.core.ChannelContext;

import java.nio.ByteBuffer;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public interface Decoder {

    Decoder decode(ByteBuffer byteBuffer, ChannelContext channelContext, Request request);

}
