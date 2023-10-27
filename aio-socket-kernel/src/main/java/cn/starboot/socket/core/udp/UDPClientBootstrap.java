package cn.starboot.socket.core.udp;

import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.ClientBootstrap;
import cn.starboot.socket.core.Packet;
import cn.starboot.socket.core.config.AioClientConfig;
import cn.starboot.socket.core.intf.AioHandler;
import cn.starboot.socket.core.jdk.aio.ImproveAsynchronousChannelGroup;
import cn.starboot.socket.core.plugins.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * UDP客户端引导程序
 *
 * @author MDong
 */
final class UDPClientBootstrap extends UDPBootstrap implements ClientBootstrap {

	private static final Logger LOGGER = LoggerFactory.getLogger(UDPClientBootstrap.class);

	UDPClientBootstrap(UDPKernelBootstrapProvider kernelBootstrapProvider) {
		super(new AioClientConfig(), kernelBootstrapProvider);
	}

	@Override
	public ChannelContext start() throws IOException {
		return null;
	}

	@Override
	public ChannelContext start(ImproveAsynchronousChannelGroup asynchronousChannelGroup) throws IOException {
		return null;
	}

	@Override
	public void shutdownNow() {

	}

	@Override
	public ClientBootstrap listen(int port) {
		return null;
	}

	@Override
	public ClientBootstrap remote(String host, int port) {
		return null;
	}

	@Override
	public ClientBootstrap addHeartPacket(Packet heartPacket) {
		return null;
	}

	@Override
	public ClientBootstrap setThreadNum(int bossThreadNum) {
		return null;
	}

	@Override
	public ClientBootstrap setMemoryPoolFactory(int size, int num, boolean useDirect) {
		return null;
	}

	@Override
	public ClientBootstrap setWriteBufferSize(int writeBufferSize, int maxWaitNum) {
		return null;
	}

	@Override
	public ClientBootstrap setReadBufferSize(int readBufferSize) {
		return null;
	}

	@Override
	public ClientBootstrap setMemoryKeep(boolean isMemoryKeep) {
		return null;
	}

	@Override
	public ClientBootstrap addPlugin(Plugin plugin) {
		return null;
	}

	@Override
	public ClientBootstrap addAioHandler(AioHandler handler) {
		return null;
	}
}
