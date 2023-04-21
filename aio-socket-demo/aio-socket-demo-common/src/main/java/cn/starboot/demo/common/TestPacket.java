package cn.starboot.demo.common;

import cn.starboot.socket.Packet;

public class TestPacket extends Packet {

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
