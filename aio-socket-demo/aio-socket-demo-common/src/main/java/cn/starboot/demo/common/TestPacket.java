package cn.starboot.demo.common;

import cn.starboot.socket.Packet;

public class TestPacket extends Packet {

	/* uid */
	private static final long serialVersionUID = -1659962481777885842L;

	private String data;

	public TestPacket(String data) {
		this.data = data;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
