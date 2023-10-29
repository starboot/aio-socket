package cn.starboot.socket.core;

import cn.starboot.socket.core.spi.KernelBootstrapProvider;

import java.io.IOException;

public interface DatagramBootstrap extends Bootstrap<DatagramBootstrap> {

	/**
	 * 启动UDP服务
	 */
	static DatagramBootstrap startUDPService() {
		return KernelBootstrapProvider.provider().openUDPBootstrap();
	}

	DatagramBootstrap start() throws IOException;

	default DatagramBootstrap listen() {
		return listen("", -1);
	}

	DatagramBootstrap listen(String host, int port);

	DatagramBootstrap remote(String host, int port);

	DatagramBootstrap addHeartPacket(Packet heartPacket);

}
