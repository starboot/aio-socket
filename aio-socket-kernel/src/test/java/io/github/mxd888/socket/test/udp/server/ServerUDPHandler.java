package io.github.mxd888.socket.test.udp.server;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.test.udp.UDPHandler;

import java.io.IOException;

public class ServerUDPHandler extends UDPHandler {

    @Override
    public Packet handle(ChannelContext channelContext, Packet packet) {
        System.out.println(packet.getResp());
        try {
            channelContext.getWriteBuffer().writeInt(16);
            channelContext.getWriteBuffer().write("hello aio-socket".getBytes());
            channelContext.getWriteBuffer().flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
