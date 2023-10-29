package cn.starboot.socket.core.udp;

import cn.starboot.socket.core.DatagramBootstrap;
import cn.starboot.socket.core.Packet;
import cn.starboot.socket.core.config.DatagramConfig;
import cn.starboot.socket.core.enums.ProtocolEnum;
import cn.starboot.socket.core.exception.AioParameterException;
import cn.starboot.socket.core.intf.AioHandler;
import cn.starboot.socket.core.jdk.nio.NioEventLoopWorker;
import cn.starboot.socket.core.plugins.Plugin;
import cn.starboot.socket.core.spi.KernelBootstrapProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 * UDP服务器引导程序
 *
 * @author MDong
 */
final class UDPBootstrapImpl extends UDPAbstractBootstrap implements DatagramBootstrap {

	private static final Logger LOGGER = LoggerFactory.getLogger(UDPBootstrapImpl.class);

	/**
	 * 默认发送的目的网络地址
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


	private ExecutorService boss_udp;

	private final NioEventLoopWorker readWorker;

	private final UDPWriteWorker writeWorker;

	private final DatagramChannel serverDatagramChannel;

	private final Function<SocketAddress, UDPChannelContext> udpChannelContextFunction;

	UDPBootstrapImpl(UDPKernelBootstrapProvider udpKernelBootstrapProvider, KernelBootstrapProvider kernelBootstrapProvider) {
		super(new DatagramConfig(), udpKernelBootstrapProvider, kernelBootstrapProvider);
		this.udpChannelContextFunction = new Function<SocketAddress, UDPChannelContext>() {
			@Override
			public UDPChannelContext apply(SocketAddress socketAddress) {
				return new UDPChannelContext(serverDatagramChannel, getConfig(), socketAddress, getWriteMemoryUnitSupplier());
			}
		};
		this.serverDatagramChannel = openDatagramChannel();
		this.readWorker = UDPReadWorker.openUDPReadWorker(udpChannelContextFunction, getConfig(), getReadMemoryUnitSupplier());
		this.writeWorker = UDPWriteWorker.openUDPWriteWorker(serverDatagramChannel);
	}

	private static DatagramChannel openDatagramChannel() {
		DatagramChannel datagramChannel = null;
		try {
			datagramChannel = DatagramChannel.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return datagramChannel;
	}


	@Override
	public DatagramBootstrap start() {
		try {
			beforeStart();
		} catch (IOException e) {
			e.printStackTrace();
		}

		start0();
		return null;
	}

	private void start0() {
		try {
			serverDatagramChannel.bind(new InetSocketAddress(getConfig().getHost(), getConfig().getPort()));
			serverDatagramChannel.configureBlocking(false);
			boss_udp = Executors.newFixedThreadPool(getConfig().getKernelThreadNumber(), r -> new Thread("boss udp"));
			boss_udp.submit(readWorker);
			boss_udp.submit(writeWorker);

			for (int i = 0; i < getConfig().getKernelThreadNumber(); i++) {
				boss_udp.submit(new UDPHandleWorker());
			}

			readWorker.addRegister(selector -> {
				try {
					serverDatagramChannel.register(selector, SelectionKey.OP_READ, UDPBootstrapImpl.this);
				} catch (ClosedChannelException closedChannelException) {
					closedChannelException.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



//	@Override
//	public void shutdown() {
//		try {
//			if (this.serverDatagramChannel != null) {
//				this.serverDatagramChannel.close();
//				this.serverDatagramChannel = null;
//			}
//			boss_udp.shutdown();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		super.shutdown();
//	}

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
//		if (this.channelContext != null) {
//			this.channelContext.close(flag);
//			this.channelContext = null;
//		}
		super.shutdown();
	}

	@Override
	public DatagramBootstrap listen(String host, int port) {
		getConfig().setHost(host);
		getConfig().setPort(port);
		return this;
	}

	@Override
	public DatagramBootstrap remote(String host, int port) {
		this.localAddress = new InetSocketAddress(host, port);
		return this;
	}

	@Override
	public DatagramBootstrap addHeartPacket(Packet heartPacket) {
		this.heartBeat = heartPacket;
		return this;
	}

	@Override
	public DatagramBootstrap setThreadNum(int threadNum) {
		try {
			getConfig().setKernelThreadNumber(threadNum);
		} catch (AioParameterException e) {
			e.printStackTrace();
		}
		return this;
	}

	@Override
	public DatagramBootstrap setMemoryPoolFactory(int size, int num, boolean useDirect) {
		getConfig().setDirect(useDirect).setMemoryBlockSize(size).setMemoryBlockNum(num);
		return this;
	}

	@Override
	public DatagramBootstrap setWriteBufferSize(int writeBufferSize, int maxWaitNum) {
		getConfig().setWriteBufferSize(writeBufferSize)
				.setMaxWaitNum(maxWaitNum);
		return this;
	}

	@Override
	public DatagramBootstrap setReadBufferSize(int readBufferSize) {
		getConfig().setReadBufferSize(readBufferSize);
		return this;
	}

	@Override
	public DatagramBootstrap setMemoryKeep(boolean isMemoryKeep) {
		getConfig().setMemoryKeep(isMemoryKeep);
		return this;
	}

	@Override
	public DatagramBootstrap addPlugin(Plugin plugin) {
		getConfig().getPlugins().addPlugin(plugin);
		return this;
	}

	@Override
	public synchronized DatagramBootstrap addAioHandler(AioHandler handler) {
		if (this.clientProtocol == null) {
			this.clientProtocol = handler.name();
		}
		getConfig().getPlugins().addAioHandler(handler);
		return this;
	}
}
