package example.client;

import example.DemoPacket;
import example.Handler;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.core.ChannelContext;


public class ClientHandler extends Handler {

    @Override
    public void handle(ChannelContext channelContext, Packet packet) {
        DemoPacket packet1 = (DemoPacket) packet;
        if (!packet1.getData().equals("hello aio-socket")) {
            System.out.println("不一致，出错啦");
        }
    }
}
