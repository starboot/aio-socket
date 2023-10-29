package cn.starboot.socket.core.udp;

import cn.starboot.socket.core.ServerBootstrap;
import cn.starboot.socket.core.config.AioServerConfig;
import cn.starboot.socket.core.intf.AioHandler;
import cn.starboot.socket.core.jdk.nio.ImproveNioSelector;
import cn.starboot.socket.core.jdk.nio.NioEventLoopWorker;
import cn.starboot.socket.core.plugins.Plugin;
import cn.starboot.socket.core.spi.KernelBootstrapProvider;
import cn.starboot.socket.core.utils.concurrent.map.ConcurrentWithMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * UDP服务器引导程序
 *
 * @author MDong
 */
final class UDPServerBootstrap extends UDPBootstrap implements ServerBootstrap {

	private static final Logger LOGGER = LoggerFactory.getLogger(UDPServerBootstrap.class);

	private DatagramChannel serverDatagramChannel;

	private final ConcurrentWithMap<SocketAddress, UDPChannelContext> channelContextHashMap = new ConcurrentWithMap<>(new HashMap<>());

	UDPServerBootstrap(UDPKernelBootstrapProvider udpKernelBootstrapProvider, KernelBootstrapProvider kernelBootstrapProvider) {
		super(new AioServerConfig(), udpKernelBootstrapProvider, kernelBootstrapProvider);
	}

	@Override
	public void start() {
		try {
			beforeStart();
		} catch (IOException e) {
			e.printStackTrace();
		}

		start0();
	}

	private ExecutorService boss_udp;
	private void start0() {
		try {
			serverDatagramChannel = DatagramChannel.open();
			serverDatagramChannel.bind(new InetSocketAddress(getConfig().getHost(), getConfig().getPort()));
			serverDatagramChannel.configureBlocking(false);
			boss_udp = Executors.newFixedThreadPool(1, r -> new Thread("boss udp"));
			NioEventLoopWorker nioEventLoopWorker = new NioEventLoopWorker(ImproveNioSelector.open(), new Consumer<SelectionKey>() {
				@Override
				public void accept(SelectionKey selectionKey) {
					if (selectionKey.isReadable()) {
						System.out.println("度");
						DatagramChannel channel = (DatagramChannel) selectionKey.channel();
						try {
							SocketAddress receive = channel.receive(null);
							channelContextHashMap.containsKey(receive, new Consumer<Boolean>() {
								@Override
								public void accept(Boolean aBoolean) {
									if (!aBoolean) {
										channelContextHashMap.put(receive, new UDPChannelContext(serverDatagramChannel, receive, null), new Consumer<UDPChannelContext>() {
											@Override
											public void accept(UDPChannelContext udpChannelContext) {
												// 用不到
											}
										});
									}
								}
							});
						} catch (IOException e) {
							e.printStackTrace();
						}
					}else if (selectionKey.isWritable()) {
						System.out.println("写");
					}
				}
			});
			boss_udp.submit(nioEventLoopWorker);
			nioEventLoopWorker.addRegister(selector -> {
				try {
					serverDatagramChannel.register(selector, SelectionKey.OP_READ, UDPServerBootstrap.this);
				} catch (ClosedChannelException closedChannelException) {
					closedChannelException.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void shutdown() {
		try {
			if (this.serverDatagramChannel != null) {
				this.serverDatagramChannel.close();
				this.serverDatagramChannel = null;
			}
			boss_udp.shutdown();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.shutdown();
	}

	@Override
	public ServerBootstrap listen(String host, int port) {
		getConfig().setHost(host);
		getConfig().setPort(port);
		return this;
	}

	@Override
	public ServerBootstrap setThreadNum(int bossThreadNum) {
		getConfig().setBossThreadNumber(bossThreadNum);
		return this;
	}

	@Override
	public ServerBootstrap setMemoryPoolFactory(int size, int num, boolean useDirect) {
		getConfig().setDirect(useDirect).setMemoryBlockSize(size).setMemoryBlockNum(num);
		return this;
	}

	@Override
	public ServerBootstrap setWriteBufferSize(int writeBufferSize, int maxWaitNum) {
		getConfig().setWriteBufferSize(writeBufferSize)
				.setMaxWaitNum(maxWaitNum);
		return this;
	}

	@Override
	public ServerBootstrap setReadBufferSize(int readBufferSize) {
		getConfig().setReadBufferSize(readBufferSize);
		return this;
	}

	@Override
	public ServerBootstrap setMemoryKeep(boolean isMemoryKeep) {
		getConfig().setMemoryKeep(isMemoryKeep);
		return this;
	}

	@Override
	public ServerBootstrap addPlugin(Plugin plugin) {
		getConfig().getPlugins().addPlugin(plugin);
		return this;
	}

	@Override
	public ServerBootstrap addAioHandler(AioHandler handler) {
		getConfig().getPlugins().addAioHandler(handler);
		return this;
	}
}
