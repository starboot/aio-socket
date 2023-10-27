package cn.starboot.socket.core;

import cn.starboot.socket.core.spi.KernelBootstrapProvider;

/**
 * aio-socket Server BootStrap
 *
 * @author MDong
 */
public interface ServerBootstrap extends Bootstrap<ServerBootstrap> {

	/**
	 * 启动TCP服务
	 */
	static ServerBootstrap startTCPService() {
		return KernelBootstrapProvider.provider().openTCPServerBootstrap();
	}

	/**
	 * 启动UDP服务
	 */
	static ServerBootstrap startUDPService() {
		return KernelBootstrapProvider.provider().openUDPServerBootstrap();
	}

	void start();

	ServerBootstrap listen(String host, int port);

}
