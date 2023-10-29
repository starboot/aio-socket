package cn.starboot.socket.core;

import cn.starboot.socket.core.jdk.aio.ImproveAsynchronousChannelGroup;
import cn.starboot.socket.core.spi.KernelBootstrapProvider;

import java.io.IOException;

public interface ClientBootstrap extends Bootstrap<ClientBootstrap> {

	/**
	 * 启动TCP服务
	 */
	static ClientBootstrap startTCPService() {
		return KernelBootstrapProvider.provider().openTCPClientBootstrap();
	}

	ChannelContext start() throws IOException;

	ChannelContext start(ImproveAsynchronousChannelGroup asynchronousChannelGroup) throws IOException;

	default ClientBootstrap listen() {
		return listen(-1);
	}

	ClientBootstrap listen(int port);

	ClientBootstrap remote(String host, int port);

	ClientBootstrap addHeartPacket(Packet heartPacket);
}
