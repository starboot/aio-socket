package cn.starboot.socket.test.udp;

import cn.starboot.socket.Packet;
import cn.starboot.socket.enums.ProtocolEnum;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.WriteBuffer;
import cn.starboot.socket.exception.AioEncoderException;
import cn.starboot.socket.intf.AioHandler;
import cn.starboot.socket.utils.pool.memory.MemoryUnit;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class UDPHandler implements AioHandler {
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
        } catch (AioEncoderException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ProtocolEnum name() {
        return ProtocolEnum.PRIVATE_UDP;
    }
}
