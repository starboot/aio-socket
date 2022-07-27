package example.server;

import example.DemoPacket;
import example.Handler;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.core.Aio;
import io.github.mxd888.socket.core.ChannelContext;



public class ServerHandler extends Handler {

    @Override
    public void handle(ChannelContext channelContext, Packet packet) {
//        System.out.println("----server:" + ((DemoPacket) packet).getData());
        if (packet instanceof DemoPacket ) {
            Aio.send(channelContext, packet);
        }

    }
}
