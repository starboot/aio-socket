package io.github.mxd888.demo.server.tcp.server;

import io.github.mxd888.demo.server.tcp.Handler;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.core.Aio;
import io.github.mxd888.socket.core.ChannelContext;



public class ServerHandler extends Handler {

    @Override
    public void handle(ChannelContext channelContext, Packet packet) {
        System.out.println("---");
        Aio.send(channelContext, packet);
    }
}
