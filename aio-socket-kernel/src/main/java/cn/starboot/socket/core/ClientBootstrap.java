package cn.starboot.socket.core;

import cn.starboot.socket.core.spi.KernelBootstrapProvider;

import java.io.IOException;

public interface ClientBootstrap extends Bootstrap<ClientBootstrap> {

	/**
	 * 启动TCP服务
	 */
	static ClientBootstrap startTCPService() {
		return KernelBootstrapProvider.provider().openTCPClientBootstrap();
	}

	/**
	 * 启动UDP服务
	 */
	static ClientBootstrap startUDPService() {
		return KernelBootstrapProvider.provider().openUDPClientBootstrap();
	}

	ChannelContext start() throws IOException;

	void shutdownNow();

	default ClientBootstrap listen() {
		return listen(-1);
	}

	ClientBootstrap listen(int port);

	ClientBootstrap remote(String host, int port);

	ClientBootstrap addHeartPacket(Packet heartPacket);
}
