package cn.starboot.socket.core;

import cn.starboot.socket.core.config.AioClientConfig;
import cn.starboot.socket.core.plugins.Plugin;
import cn.starboot.socket.core.spi.KernelBootstrapProvider;

public abstract class ClientBootstrap extends AbstractBootstrap {

	private final KernelBootstrapProvider kernelBootstrapProvider;

	protected ClientBootstrap(KernelBootstrapProvider kernelBootstrapProvider) {
		super(new AioClientConfig());
		this.kernelBootstrapProvider = kernelBootstrapProvider;
	}

	public final KernelBootstrapProvider provider() {
		return kernelBootstrapProvider;
	}

	/**
	 * 启动TCP服务
	 */
	public static ClientBootstrap startTCPService() {

		return null;
	}

	/**
	 * 启动UDP服务
	 */
	public static ClientBootstrap startUDPService() {

		return null;
	}

	public abstract ChannelContext start();

	public abstract void shutdownNow();

	public ClientBootstrap listen() {
		return listen(-1);
	}

	public abstract ClientBootstrap listen(int port);

	public abstract ClientBootstrap setThreadNum(int bossThreadNum);

	public abstract ClientBootstrap setMemoryPoolFactory(int size, int num, boolean useDirect);

	public abstract ClientBootstrap setWriteBufferSize(int writeBufferSize, int maxWaitNum);

	public abstract ClientBootstrap setReadBufferSize(int readBufferSize);

	public abstract ClientBootstrap setMemoryKeep(boolean isMemoryKeep);

	public abstract ClientBootstrap addPlugin(Plugin plugin);

	public abstract ClientBootstrap addAioHandler(String host, int port);

	public abstract ClientBootstrap addHeartPacket(Packet heartPacket);
}
