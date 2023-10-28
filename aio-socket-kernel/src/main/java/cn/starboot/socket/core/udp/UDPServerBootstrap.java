package cn.starboot.socket.core.udp;

import cn.starboot.socket.core.ServerBootstrap;
import cn.starboot.socket.core.config.AioServerConfig;
import cn.starboot.socket.core.intf.AioHandler;
import cn.starboot.socket.core.plugins.Plugin;
import cn.starboot.socket.core.spi.KernelBootstrapProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UDP服务器引导程序
 *
 * @author MDong
 */
final class UDPServerBootstrap extends UDPBootstrap implements ServerBootstrap {

	private static final Logger LOGGER = LoggerFactory.getLogger(UDPServerBootstrap.class);

	UDPServerBootstrap(UDPKernelBootstrapProvider udpKernelBootstrapProvider, KernelBootstrapProvider kernelBootstrapProvider) {
		super(new AioServerConfig(), udpKernelBootstrapProvider, kernelBootstrapProvider);
	}

	@Override
	public void start() {

	}

	@Override
	public ServerBootstrap listen(String host, int port) {
		return null;
	}

	@Override
	public ServerBootstrap setThreadNum(int bossThreadNum) {
		return null;
	}

	@Override
	public ServerBootstrap setMemoryPoolFactory(int size, int num, boolean useDirect) {
		return null;
	}

	@Override
	public ServerBootstrap setWriteBufferSize(int writeBufferSize, int maxWaitNum) {
		return null;
	}

	@Override
	public ServerBootstrap setReadBufferSize(int readBufferSize) {
		return null;
	}

	@Override
	public ServerBootstrap setMemoryKeep(boolean isMemoryKeep) {
		return null;
	}

	@Override
	public ServerBootstrap addPlugin(Plugin plugin) {
		return null;
	}

	@Override
	public ServerBootstrap addAioHandler(AioHandler handler) {
		return null;
	}
}
