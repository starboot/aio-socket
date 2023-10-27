package cn.starboot.socket.core;

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

		return null;
	}

	/**
	 * 启动UDP服务
	 */
	static ServerBootstrap startUDPService() {

		return null;
	}

	void start();

	ServerBootstrap listen(String host, int port);

}
