package io.github.mxd888.demo.common;

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
        switch (stateMachineEnum){
            case DECODE_EXCEPTION:
                System.out.println("解码异常");
                break;
            case PROCESS_EXCEPTION:
                System.out.println("处理异常");
                break;
            case INPUT_SHUTDOWN:
                System.out.println("输入流已关闭");
                break;
        }
    }
}
