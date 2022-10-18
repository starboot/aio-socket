package io.github.mxd888.socket.test.udp;

import io.github.mxd888.socket.Packet;

public class UDPPacket extends Packet {

    private final String data;

    public UDPPacket(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
