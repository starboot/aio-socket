package io.github.mxd888.socket.codec.base64;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.exception.AioDecoderException;
import io.github.mxd888.socket.intf.Handler;
import io.github.mxd888.socket.utils.pool.memory.MemoryUnit;

public abstract class Base64Handler implements Handler {

    @Override
    public abstract Packet handle(ChannelContext channelContext, Packet packet);

    @Override
    public Packet decode(MemoryUnit readBuffer, ChannelContext channelContext) throws AioDecoderException {
        return null;
    }

    @Override
    public void encode(Packet packet, ChannelContext channelContext) {

    }
}
