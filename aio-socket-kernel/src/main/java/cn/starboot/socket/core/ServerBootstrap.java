package cn.starboot.socket.core;

import cn.starboot.socket.core.spi.KernelBootstrapProvider;

/**
 * aio-socket Server BootStrap
 *
 * @author MDong
 */
public abstract class ServerBootstrap {

	private final KernelBootstrapProvider kernelBootstrapProvider;

	protected ServerBootstrap(KernelBootstrapProvider kernelBootstrapProvider) {
		this.kernelBootstrapProvider = kernelBootstrapProvider;
	}

	public final KernelBootstrapProvider provider() {
		return kernelBootstrapProvider;
	}

	/**
	 * 启动TCP服务
	 */
	public static void startTCPService() {

	}

	/**
	 * 启动UDP服务
	 */
	public static void startUDPService() {

	}
}
