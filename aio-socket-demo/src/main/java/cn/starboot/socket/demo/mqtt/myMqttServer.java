package cn.starboot.socket.demo.mqtt;

import cn.starboot.socket.core.tcp.ServerBootstrap;

public class myMqttServer {

	public static void main(String[] args) {
		ServerBootstrap bootstrap = new ServerBootstrap("localhost", 8888, new myMQTTHandler());
		bootstrap.setMemoryPoolFactory(2 * 1024 * 1024, 2, true)
				.setReadBufferSize(1024 * 1024)
				.setWriteBufferSize(1024 * 4, 512)
				.start();
	}
}
