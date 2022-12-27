package cn.starboot.socket.test.udp.server;

import cn.starboot.socket.test.udp.UDPPacket;
import cn.starboot.socket.Packet;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.test.udp.UDPHandler;

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
