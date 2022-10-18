package io.github.mxd888.socket.codec.bytes;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.intf.IProtocol;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.exception.AioDecoderException;
import io.github.mxd888.socket.intf.Handler;
import io.github.mxd888.socket.ProtocolEnum;
import io.github.mxd888.socket.utils.pool.memory.MemoryUnit;

public abstract class BytesHandler implements Handler, IProtocol {

    @Override
    public abstract Packet handle(ChannelContext channelContext, Packet packet);

    @Override
    public Packet decode(MemoryUnit readBuffer, ChannelContext channelContext) throws AioDecoderException {
        return null;
    }

    @Override
    public void encode(Packet packet, ChannelContext channelContext) {

    }

    @Override
    public ProtocolEnum name() {
        return ProtocolEnum.BYTES;
    }
}
