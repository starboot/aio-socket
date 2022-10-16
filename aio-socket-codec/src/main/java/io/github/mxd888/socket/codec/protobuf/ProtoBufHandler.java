package io.github.mxd888.socket.codec.protobuf;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.exception.AioDecoderException;
import io.github.mxd888.socket.intf.AioHandler;
import io.github.mxd888.socket.utils.pool.memory.MemoryUnit;

public abstract class ProtoBufHandler<T> implements AioHandler<T> {

    @Override
    public abstract Packet<T> handle(ChannelContext channelContext, Packet<T> packet);

    @Override
    public Packet<T> decode(MemoryUnit readBuffer, ChannelContext channelContext) throws AioDecoderException {
        return null;
    }

    @Override
    public void encode(Packet<T> packet, ChannelContext channelContext) {

    }
}
