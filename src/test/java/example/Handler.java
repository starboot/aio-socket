package example;

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
    public Packet decode(VirtualBuffer virtualBuffer, ChannelContext channelContext, Packet packet) {
        ByteBuffer buffer = virtualBuffer.buffer();
        int remaining = buffer.remaining();
        if (remaining < Integer.BYTES) {
            buffer.reset();
            return null;
        }
        int length = buffer.getInt();
        if (length > buffer.remaining()) {
            buffer.reset();
            return null;
        }
        byte[] b = new byte[length];
        buffer.get(b);
        DemoPacket demoPacket = new DemoPacket(new String(b));
        if (packet.getFromId() != null) {
            demoPacket.setFromId(packet.getFromId());
        }
        if (packet.getToId() != null) {
            demoPacket.setToId(packet.getToId());
        }

//        demoPacket.setFromId("15100101677");
//        demoPacket.setToId("15511090451");
        return demoPacket;
    }

    @Override
    public VirtualBuffer encode(Packet packet, ChannelContext channelContext, VirtualBuffer virtualBuffer) {
        if (packet instanceof DemoPacket) {
            DemoPacket demoPacket = (DemoPacket) packet;
            // 自定义协议
            ByteBuffer byteBuf = virtualBuffer.buffer();
            byteBuf.putInt(demoPacket.getData().getBytes().length);
            byteBuf.put(demoPacket.getData().getBytes());
            byteBuf.flip();
//            System.out.println("编码后的长度" + byteBuf.remaining() + "---" + packet.getToId());
            return virtualBuffer;
        }

        ByteBuffer byteBuf = virtualBuffer.buffer();
        byteBuf.flip();
        return virtualBuffer;
    }

    @Override
    public void stateEvent(ChannelContext channelContext, StateMachineEnum stateMachineEnum, Throwable throwable) {

    }
}
