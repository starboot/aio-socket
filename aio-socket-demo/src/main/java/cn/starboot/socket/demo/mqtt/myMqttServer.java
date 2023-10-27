package cn.starboot.socket.demo.mqtt;

import cn.starboot.socket.core.tcp.TCPServerBootstrap;

public class myMqttServer {

	public static void main(String[] args) {
		TCPServerBootstrap bootstrap = new TCPServerBootstrap("localhost", 8888, new myMQTTHandler());
		bootstrap.setMemoryPoolFactory(2 * 1024 * 1024, 2, true)
				.setReadBufferSize(1024 * 1024)
				.setWriteBufferSize(1024 * 4, 512)
				.start();
	}
}
