package io.github.mxd888.demo.common;

import cn.hutool.core.util.ByteUtil;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.StateMachineEnum;
import io.github.mxd888.socket.buffer.VirtualBuffer;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.core.WriteBuffer;
import io.github.mxd888.socket.intf.AioHandler;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Handler implements AioHandler {

    @Override
    public Packet handle(ChannelContext channelContext, Packet packet) {
        return null;
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
            WriteBuffer writeBuffer1 = channelContext.getWriteBuffer();
//            ByteBuffer byteBuf = writeBuffer.buffer();
            try {
                writeBuffer1.writeInt(demoPacket.getData().getBytes().length);
                writeBuffer1.write(demoPacket.getData().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

//            return writeBuffer;
        }
        return null;
    }

    @Override
    public void stateEvent(ChannelContext channelContext, StateMachineEnum stateMachineEnum, Throwable throwable) {
        switch (stateMachineEnum){
            case DECODE_EXCEPTION:
                System.out.println("解码异常");
                throwable.printStackTrace();
                break;
            case PROCESS_EXCEPTION:
                System.out.println("处理异常");
                break;
            case INPUT_SHUTDOWN:
                System.out.println("输入流已关闭");
                break;
            case REJECT_ACCEPT:
                System.out.println("超过负载，拒绝连接");
                break;
        }
    }
}
