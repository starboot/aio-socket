package cn.starboot.socket.test.udp.client;

import cn.starboot.socket.core.Packet;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.test.udp.UDPHandler;
import cn.starboot.socket.test.udp.UDPPacket;

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
