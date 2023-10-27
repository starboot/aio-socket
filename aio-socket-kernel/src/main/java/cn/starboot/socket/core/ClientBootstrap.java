package cn.starboot.socket.core;

import java.io.IOException;

public interface ClientBootstrap extends Bootstrap<ClientBootstrap> {

	/**
	 * 启动TCP服务
	 */
	static ClientBootstrap startTCPService() {

		return null;
	}

	/**
	 * 启动UDP服务
	 */
	static ClientBootstrap startUDPService() {

		return null;
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
