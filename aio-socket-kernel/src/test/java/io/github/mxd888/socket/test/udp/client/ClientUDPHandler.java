package io.github.mxd888.socket.test.udp.client;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.test.udp.UDPHandler;

public class ClientUDPHandler extends UDPHandler {
    @Override
    public Packet handle(ChannelContext channelContext, Packet packet) {
        System.out.println(packet.getResp());
        return null;
    }
}
