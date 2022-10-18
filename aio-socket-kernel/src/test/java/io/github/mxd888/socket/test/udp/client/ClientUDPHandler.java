package io.github.mxd888.socket.test.udp.client;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.test.udp.UDPHandler;
import io.github.mxd888.socket.test.udp.UDPPacket;

public class ClientUDPHandler extends UDPHandler {
    @Override
    public Packet handle(ChannelContext channelContext, Packet packet) {
        if (packet instanceof UDPPacket) {
            UDPPacket packet1 = (UDPPacket) packet;
            System.out.println(packet1.getData());
        }
        return null;
    }
}
