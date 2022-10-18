package io.github.mxd888.socket.test.udp;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.ProtocolEnum;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.core.WriteBuffer;
import io.github.mxd888.socket.intf.AioHandler;
import io.github.mxd888.socket.intf.Handler;
import io.github.mxd888.socket.utils.pool.memory.MemoryUnit;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class UDPHandler extends AioHandler {
    @Override
    public Packet handle(ChannelContext channelContext, Packet packet) {
        return null;
    }

    @Override
    public Packet decode(MemoryUnit readBuffer, ChannelContext channelContext) {
        ByteBuffer buffer = readBuffer.buffer();
        int remaining = buffer.remaining();
        if (remaining < Integer.BYTES) {
            return null;
        }
        buffer.mark();
        int length = buffer.getInt();
        if (length > buffer.remaining()) {
            buffer.reset();
            return null;
        }
        byte[] b = new byte[length];
        buffer.get(b);
        return new UDPPacket(new String(b, StandardCharsets.UTF_8));
    }

    @Override
    public void encode(Packet packet, ChannelContext channelContext) {
        WriteBuffer writeBuffer = channelContext.getWriteBuffer();
        try {
            UDPPacket packet1 = (UDPPacket) packet;
            writeBuffer.writeInt(packet1.getData().getBytes().length);
            writeBuffer.write(packet1.getData().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ProtocolEnum name() {
        return ProtocolEnum.PRIVATE_UDP;
    }
}
