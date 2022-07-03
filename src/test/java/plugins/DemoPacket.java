package plugins;

import io.github.mxd888.socket.Packet;

public class DemoPacket extends Packet {
    private String msg;

    public DemoPacket(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
