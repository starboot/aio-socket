package io.github.mxd888.demo.server.tcp.server;

import io.github.mxd888.demo.server.tcp.DemoPacket;
import io.github.mxd888.demo.server.tcp.Handler;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.core.Aio;
import io.github.mxd888.socket.core.ChannelContext;



public class ServerHandler extends Handler {

    @Override
    public void handle(ChannelContext channelContext, Packet packet) {
        if (packet instanceof DemoPacket) {
            DemoPacket packet1 = (DemoPacket) packet;
            System.out.println(packet1.getData());
        }
        Aio.send(channelContext, packet);
    }
}
