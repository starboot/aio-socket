package io.github.mxd888.socket.test.udp.server;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.test.udp.UDPHandler;
import io.github.mxd888.socket.test.udp.UDPPacket;

import java.io.IOException;

public class ServerUDPHandler extends UDPHandler {

    @Override
    public Packet handle(ChannelContext channelContext, Packet packet) {
        if (packet instanceof UDPPacket) {
            UDPPacket packet1 = (UDPPacket) packet;
            System.out.println(packet1.getData());
        }
        return packet;
    }
}
