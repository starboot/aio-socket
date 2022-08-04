package io.github.mxd888.demo.server.tcp;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.StateMachineEnum;
import io.github.mxd888.socket.buffer.VirtualBuffer;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.intf.AioHandler;

import java.nio.ByteBuffer;

public class Handler implements AioHandler {

    @Override
    public void handle(ChannelContext channelContext, Packet packet) {

    }

    @Override
    public Packet decode(VirtualBuffer virtualBuffer, ChannelContext channelContext) {
        ByteBuffer buffer = virtualBuffer.buffer();
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
        return new DemoPacket(new String(b));
    }

    @Override
    public VirtualBuffer encode(Packet packet, ChannelContext channelContext, VirtualBuffer writeBuffer) {
        if (packet instanceof DemoPacket) {
            DemoPacket demoPacket = (DemoPacket) packet;
            // 自定义协议
            ByteBuffer byteBuf = writeBuffer.buffer();
            byteBuf.putInt(demoPacket.getData().getBytes().length);
            byteBuf.put(demoPacket.getData().getBytes());
            byteBuf.flip();
            return writeBuffer;
        }
        return null;
    }

    @Override
    public void stateEvent(ChannelContext channelContext, StateMachineEnum stateMachineEnum, Throwable throwable) {

    }
}
