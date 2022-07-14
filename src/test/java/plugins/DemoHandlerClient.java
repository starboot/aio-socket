package plugins;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.StateMachineEnum;
import io.github.mxd888.socket.buffer.VirtualBuffer;
import io.github.mxd888.socket.intf.AioHandler;
import io.github.mxd888.socket.core.ChannelContext;

import java.nio.ByteBuffer;

public class DemoHandlerClient implements AioHandler {
    @Override
    public void handle(ChannelContext channelContext, Packet packet) {
        if (!"hello".equals(packet.getFromId())) {
            System.out.println("消息出错啦");
        }

        System.out.println(packet.getFromId());
//        WriteBuffer outputStream = channelContext.writeBuffer();
//        try {
//            byte[] bytes = ((DemoPacket) packet).getMsg().getBytes();
//            outputStream.writeInt(bytes.length);
//            outputStream.write(bytes);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public Packet decode(VirtualBuffer readBuffer, ChannelContext channelContext) {
        int remaining = readBuffer.buffer().remaining();
        if (remaining < Integer.BYTES) {
            return null;
        }
        readBuffer.buffer().mark();
        int length = readBuffer.buffer().getInt();
        if (length > readBuffer.buffer().remaining()) {
            readBuffer.buffer().reset();
            return null;
        }
        byte[] b = new byte[length];
        readBuffer.buffer().get(b);
        readBuffer.buffer().mark();
        Packet packet = new Packet();
        packet.setFromId(new String(b));
        return packet;

    }

    @Override
    public VirtualBuffer encode(Packet packet, ChannelContext channelContext) {
        VirtualBuffer virtualBuffer = channelContext.getByteBuf();
        ByteBuffer byteBuf = virtualBuffer.buffer();
//        System.out.println(packet.getFromId());
        byteBuf.putInt(packet.getFromId().getBytes().length);
        byteBuf.put(packet.getFromId().getBytes());
        byteBuf.flip();
        return virtualBuffer;
    }

    @Override
    public void stateEvent(ChannelContext channelContext, StateMachineEnum stateMachineEnum, Throwable throwable) {
//        System.out.println("chufa");
    }
}
