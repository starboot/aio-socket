package cn.starboot.socket.core.udp;

import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.ClientBootstrap;
import cn.starboot.socket.core.Packet;
import cn.starboot.socket.core.config.AioClientConfig;
import cn.starboot.socket.core.enums.ProtocolEnum;
import cn.starboot.socket.core.intf.AioHandler;
import cn.starboot.socket.core.jdk.aio.ImproveAsynchronousChannelGroup;
import cn.starboot.socket.core.plugins.Plugin;
import cn.starboot.socket.core.spi.KernelBootstrapProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;

/**
 * UDP客户端引导程序
 *
 * @author MDong
 */
final class UDPClientBootstrap extends UDPBootstrap implements ClientBootstrap {

	private static final Logger LOGGER = LoggerFactory.getLogger(UDPClientBootstrap.class);

	private UDPChannelContext channelContext;

//	private DatagramChannel clientDatagramChannel;

	/**
	 * 绑定本地地址
	 */
	private SocketAddress localAddress;

	/**
	 * 客户端所用协议
	 */
	private ProtocolEnum clientProtocol;

	/**
	 * 心跳包
	 */
	private Packet heartBeat = null;

	UDPClientBootstrap(UDPKernelBootstrapProvider udpKernelBootstrapProvider, KernelBootstrapProvider kernelBootstrapProvider) {
		super(new AioClientConfig(), udpKernelBootstrapProvider, kernelBootstrapProvider);
	}

	@Override
	public ChannelContext start() throws IOException {
		return start(null);
	}

	@Override
	public ChannelContext start(ImproveAsynchronousChannelGroup asynchronousChannelGroup) throws IOException {

		return null;
	}

	@Override
	public final void shutdown() {
		shutdown0(false);
	}

	/**
	 * 立即关闭客户端
	 */
	@Override
	public final void shutdownNow() {
		shutdown0(true);
	}

	private void shutdown0(boolean flag) {
		if (this.channelContext != null) {
			this.channelContext.close(flag);
			this.channelContext = null;
		}
		super.shutdown();
	}

	@Override
	public ClientBootstrap listen(int port) {
		this.localAddress = new InetSocketAddress(port);
		return this;
	}

	@Override
	public ClientBootstrap remote(String host, int port) {
		getConfig().setHost(host);
		getConfig().setPort(port);
		return this;
	}

	@Override
	public ClientBootstrap addHeartPacket(Packet heartPacket) {
		this.heartBeat = heartPacket;
		return this;
	}

	@Override
	public ClientBootstrap setThreadNum(int bossThreadNum) {
		getConfig().setBossThreadNumber(bossThreadNum);
		return this;
	}

	@Override
	public ClientBootstrap setMemoryPoolFactory(int size, int num, boolean useDirect) {
		getConfig().setDirect(useDirect).setMemoryBlockSize(size).setMemoryBlockNum(num);
		return this;
	}

	@Override
	public ClientBootstrap setWriteBufferSize(int writeBufferSize, int maxWaitNum) {
		getConfig().setWriteBufferSize(writeBufferSize)
				.setMaxWaitNum(maxWaitNum);
		return this;
	}

	@Override
	public ClientBootstrap setReadBufferSize(int readBufferSize) {
		getConfig().setReadBufferSize(readBufferSize);
		return this;
	}

	@Override
	public ClientBootstrap setMemoryKeep(boolean isMemoryKeep) {
		getConfig().setMemoryKeep(isMemoryKeep);
		return this;
	}

	@Override
	public ClientBootstrap addPlugin(Plugin plugin) {
		getConfig().getPlugins().addPlugin(plugin);
		return this;
	}

	@Override
	public synchronized ClientBootstrap addAioHandler(AioHandler handler) {
		if (this.clientProtocol != null) {
			String err = "ClientBootstrap can only call addAioHandler once";
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error(err);
			}else {
				System.err.println(err);
			}
			return this;
		}
		getConfig().getPlugins().addAioHandler(handler);
		this.clientProtocol = handler.name();
		return this;
	}
}
