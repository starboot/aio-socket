package cn.starboot.socket.test.udp;

import cn.starboot.socket.Packet;

public class UDPPacket extends Packet {

    private final String data;

    public UDPPacket(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
