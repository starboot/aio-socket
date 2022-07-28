package demo;

import io.github.mxd888.socket.Packet;

public class DemoPacket extends Packet {

    private String data;

    public DemoPacket(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
