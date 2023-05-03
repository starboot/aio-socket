package cn.starboot.socket.test.udp;

import cn.starboot.socket.Packet;

public class UDPPacket extends Packet {

	/* uid */
	private static final long serialVersionUID = 2869517132259909209L;

	private final String data;

    public UDPPacket(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
