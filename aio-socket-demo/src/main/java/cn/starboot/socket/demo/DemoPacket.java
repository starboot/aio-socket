package cn.starboot.socket.demo;

import cn.starboot.socket.core.Packet;

public class DemoPacket extends Packet {

	/* uid */
	private static final long serialVersionUID = 5950674098701714314L;

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
