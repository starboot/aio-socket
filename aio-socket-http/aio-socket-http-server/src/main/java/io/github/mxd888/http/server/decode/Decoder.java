package io.github.mxd888.http.server.decode;

import io.github.mxd888.http.server.impl.Request;
import io.github.mxd888.socket.core.ChannelContext;

import java.nio.ByteBuffer;

/**
 * @author 三刀
 * @version V1.0 , 2020/3/30
 */
public interface Decoder {

    Decoder decode(ByteBuffer byteBuffer, ChannelContext channelContext, Request request);

}
