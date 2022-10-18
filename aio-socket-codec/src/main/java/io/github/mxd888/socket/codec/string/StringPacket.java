package io.github.mxd888.socket.codec.string;

import io.github.mxd888.socket.Packet;

public class StringPacket extends Packet {

    private String data;

    public StringPacket(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
