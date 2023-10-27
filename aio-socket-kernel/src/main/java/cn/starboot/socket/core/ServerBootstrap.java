package cn.starboot.socket.core;

import cn.starboot.socket.core.plugins.Plugin;
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

	public abstract void start();

	public abstract void shutdown();

	public abstract ServerBootstrap listen(String host, int port);

	public abstract ServerBootstrap setThreadNum(int bossThreadNum);

	public abstract ServerBootstrap setMemoryPoolFactory(int size, int num, boolean useDirect);

	public abstract ServerBootstrap setWriteBufferSize(int writeBufferSize, int maxWaitNum);

	public abstract ServerBootstrap setReadBufferSize(int readBufferSize);

	public abstract ServerBootstrap setMemoryKeep(boolean isMemoryKeep);

	public abstract ServerBootstrap addPlugin(Plugin plugin);

	public abstract ServerBootstrap addAioHandler(String host, int port);

}
