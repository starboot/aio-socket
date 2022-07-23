//package plugins;
//
//import io.github.mxd888.socket.Packet;
//import io.github.mxd888.socket.StateMachineEnum;
//import io.github.mxd888.socket.buffer.VirtualBuffer;
//import io.github.mxd888.socket.intf.AioHandler;
//import io.github.mxd888.socket.core.ChannelContext;
//
//public class DemoHandler implements AioHandler {
//    @Override
//    public void handle(ChannelContext channelContext, Packet packet) {
//        channelContext.close();
////        System.out.println("receive from client: " + ((DemoPacket) packet).getMsg());
////        WriteBuffer outputStream = channelContext.writeBuffer();
////        try {
////            byte[] bytes = ((DemoPacket) packet).getMsg().getBytes();
//////            outputStream.writeInt(bytes.length);
//////            outputStream.write(bytes);
//////            outputStream.flush();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//    }
//
//    @Override
//    public Packet decode(VirtualBuffer readBuffer, ChannelContext channelContext) {
//        int remaining = readBuffer.buffer().remaining();
//        if (remaining < Integer.BYTES) {
//            return null;
//        }
//        readBuffer.buffer().mark();
//        int length = readBuffer.buffer().getInt();
//        if (length > readBuffer.buffer().remaining()) {
//            readBuffer.buffer().reset();
//            return null;
//        }
//        byte[] b = new byte[length];
//        readBuffer.buffer().get(b);
//        readBuffer.buffer().mark();
//        return new DemoPacket(new String(b));
//
//    }
//
//    @Override
//    public VirtualBuffer encode(Packet packet, ChannelContext channelContext) {
//        return null;
//    }
//
//    @Override
//    public void stateEvent(ChannelContext channelContext, StateMachineEnum stateMachineEnum, Throwable throwable) {
////        System.out.println("chufa");
//    }
//}
