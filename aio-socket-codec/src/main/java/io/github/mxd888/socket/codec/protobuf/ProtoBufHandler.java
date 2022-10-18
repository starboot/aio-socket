package io.github.mxd888.socket.codec.protobuf;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.exception.AioDecoderException;
import io.github.mxd888.socket.intf.AioHandler;
import io.github.mxd888.socket.utils.pool.memory.MemoryUnit;

public abstract class ProtoBufHandler implements AioHandler {

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
