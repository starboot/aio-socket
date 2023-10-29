package cn.starboot.socket.core.udp;

import cn.starboot.socket.core.DatagramBootstrap;
import cn.starboot.socket.core.Packet;
import cn.starboot.socket.core.config.AioServerConfig;
import cn.starboot.socket.core.enums.ProtocolEnum;
import cn.starboot.socket.core.intf.AioHandler;
import cn.starboot.socket.core.jdk.nio.ImproveNioSelector;
import cn.starboot.socket.core.jdk.nio.NioEventLoopWorker;
import cn.starboot.socket.core.plugins.Plugin;
import cn.starboot.socket.core.spi.KernelBootstrapProvider;
import cn.starboot.socket.core.utils.concurrent.map.ConcurrentWithMap;
import cn.starboot.socket.core.utils.pool.memory.MemoryUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

/**
 * UDP服务器引导程序
 *
 * @author MDong
 */
final class UDPBootstrap extends UDPAbstractBootstrap implements DatagramBootstrap {

	private static final Logger LOGGER = LoggerFactory.getLogger(UDPBootstrap.class);

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

	private DatagramChannel serverDatagramChannel;

	private final Semaphore writeSemaphore = new Semaphore(1);

	private final ConcurrentWithMap<SocketAddress, UDPChannelContext> channelContextHashMap = new ConcurrentWithMap<>(new HashMap<>());

	private final ConcurrentLinkedQueue<MemoryUnit> writeQueue = new ConcurrentLinkedQueue<>();

	UDPBootstrap(UDPKernelBootstrapProvider udpKernelBootstrapProvider, KernelBootstrapProvider kernelBootstrapProvider) {
		super(new AioServerConfig(), udpKernelBootstrapProvider, kernelBootstrapProvider);
		nioEventLoopWorker = new NioEventLoopWorker(ImproveNioSelector.open(), new Consumer<SelectionKey>() {
			@Override
			public void accept(SelectionKey selectionKey) {
				handle0(selectionKey);
			}
		});
	}

	private void handle0(SelectionKey selectionKey) {
		if (selectionKey.isReadable()) {
			System.out.println("度");
			DatagramChannel channel = (DatagramChannel) selectionKey.channel();
			try {
				// 申请内存
				final MemoryUnit readMemoryUnit = readMemoryUnitFactory.createMemoryUnit(memoryPool.allocateMemoryBlock());
				readMemoryUnit.buffer().clear();
				SocketAddress receive = channel.receive(readMemoryUnit.buffer());
				channelContextHashMap.containsKey(receive, new Consumer<Boolean>() {
					@Override
					public void accept(Boolean aBoolean) {
						if (aBoolean) {
							channelContextHashMap.get(receive, new Consumer<UDPChannelContext>() {
								@Override
								public void accept(UDPChannelContext udpChannelContext) {
									udpChannelContext.addMemoryUnit(readMemoryUnit).handle();
								}
							});
						} else {
							channelContextHashMap.put(
									receive,
									new UDPChannelContext(serverDatagramChannel,
											getConfig(),
											receive,
											null,
											nioEventLoopWorker),
									new Consumer<UDPChannelContext>() {
										@Override
										public void accept(UDPChannelContext udpChannelContext) {
											udpChannelContext.addMemoryUnit(readMemoryUnit).handle();
										}
									});
						}
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (selectionKey.isWritable()) {
			selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
			UDPChannelContext attachment = (UDPChannelContext) selectionKey.attachment();
			attachment.doWrite();
		}
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

	private ExecutorService boss_udp;
	private final NioEventLoopWorker nioEventLoopWorker;

	private void start0() {
		try {
			serverDatagramChannel = DatagramChannel.open();
			serverDatagramChannel.bind(new InetSocketAddress(getConfig().getHost(), getConfig().getPort()));
			serverDatagramChannel.configureBlocking(false);
			boss_udp = Executors.newFixedThreadPool(1, r -> new Thread("boss udp"));
			boss_udp.submit(nioEventLoopWorker);
			nioEventLoopWorker.addRegister(selector -> {
				try {
					serverDatagramChannel.register(selector, SelectionKey.OP_READ, UDPBootstrap.this);
				} catch (ClosedChannelException closedChannelException) {
					closedChannelException.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void doWrite(MemoryUnit writeMemoryUnit, SocketAddress remote) {
		try {
			if (!writeSemaphore.tryAcquire()) {
				// 不可以写，存下来一会写，存remote
				writeQueue.offer(writeMemoryUnit);
				return;
			}
			int send = serverDatagramChannel.send(writeMemoryUnit.buffer(), remote);

			if (send > 0) {
				System.out.println("写入成功");
			}

			if (send == 0) {
				// 无法写
				nioEventLoopWorker.addRegister(new Consumer<Selector>() {
					@Override
					public void accept(Selector selector) {
						try {
							serverDatagramChannel.register(selector, SelectionKey.OP_WRITE, this);
						} catch (ClosedChannelException closedChannelException) {
							closedChannelException.printStackTrace();
						}
					}
				});
			}
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
		this.localAddress = new InetSocketAddress(host, port);
		return this;
	}

	@Override
	public DatagramBootstrap remote(String host, int port) {
		return null;
	}

	@Override
	public DatagramBootstrap addHeartPacket(Packet heartPacket) {
		this.heartBeat = heartPacket;
		return this;
	}

	@Override
	public DatagramBootstrap setThreadNum(int bossThreadNum) {
		getConfig().setBossThreadNumber(bossThreadNum);
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
