/*
 *    Copyright 2019 The aio-socket Project
 *
 *    The aio-socket Project Licenses this file to you under the Apache License,
 *    Version 2.0 (the "License"); you may not use this file except in compliance
 *    with the License. You may obtain a copy of the License at:
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package cn.starboot.socket.core;

import cn.starboot.socket.enums.ProtocolEnum;
import cn.starboot.socket.config.AioClientConfig;
import cn.starboot.socket.utils.ThreadUtils;
import cn.starboot.socket.utils.pool.memory.MemoryPool;
import cn.starboot.socket.Packet;
import cn.starboot.socket.intf.AioHandler;
import cn.starboot.socket.plugins.Plugin;
import cn.starboot.socket.utils.pool.memory.MemoryPoolFactory;
import cn.starboot.socket.utils.pool.memory.MemoryUnit;
import cn.starboot.socket.utils.pool.memory.MemoryUnitFactory;
import cn.starboot.socket.plugins.Plugins;
import cn.starboot.socket.utils.AIOUtil;
import cn.starboot.socket.utils.TimerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * AIO Client 启动类
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class ClientBootstrap {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientBootstrap.class);

	/**
	 * 重连插件使用
	 */
	private boolean isCheck = true;

	/**
	 * 心跳包
	 */
	private Packet heartBeat = null;

	/**
	 * 绑定本地地址
	 */
	private SocketAddress localAddress;

	/**
	 * 客户端所用协议
	 */
	private final ProtocolEnum clientProtocol;

	/**
	 * 网络连接的会话对象
	 */
	private TCPChannelContext channelContext;

	/**
	 * 内存池
	 */
	private MemoryPool memoryPool = null;

	/**
	 * 连接超时时间
	 */
	private final static int connectTimeout = 5000;

	/**
	 * IO事件处理线程组。
	 * 作为客户端，该AsynchronousChannelGroup只需保证2个长度的线程池大小即可满足通信读写所需。
	 */
	private AsynchronousChannelGroup asynchronousChannelGroup;

	/**
	 * 客户端服务配置。
	 */
	private final AioConfig config = new AioClientConfig();

	/**
	 * 构造虚拟缓冲区工厂
	 */
	private final MemoryUnitFactory readMemoryUnitFactory = memoryBlock -> memoryBlock.allocate(this.config.getReadBufferSize());

	/**
	 * 当前构造方法设置了启动Aio客户端的必要参数，基本实现开箱即用。
	 *
	 * @param host    远程服务器地址
	 * @param port    远程服务器端口号
	 * @param handler 协议编解码  消息处理器
	 */
	public ClientBootstrap(String host, int port, AioHandler handler) {
		this.config.setHost(host);
		this.config.setPort(port);
		this.config.getPlugins().addAioHandler(handler);
		this.clientProtocol = handler.name();
	}

	/**
	 * 启动客户端。
	 * 本方法会构建线程数为threadNum的{@code asynchronousChannelGroup}
	 * 并通过调用{@link cn.starboot.socket.core.ClientBootstrap#start(AsynchronousChannelGroup)}启动服务。
	 *
	 * @return 建立连接后的会话对象
	 * @throws IOException IOException
	 * @see cn.starboot.socket.core.ClientBootstrap#start(AsynchronousChannelGroup)
	 */
	public final ChannelContext start() throws IOException {
		AsynchronousChannelProvider provider = AsynchronousChannelProvider.provider();
		this.asynchronousChannelGroup = provider.openAsynchronousChannelGroup(ThreadUtils.getGroupExecutor(getConfig().getBossThreadNumber()), getConfig().getBossThreadNumber());
		do {
			start(this.asynchronousChannelGroup);

		} while (this.channelContext == null);
		return this.channelContext;
	}

	/**
	 * 启动客户端。
	 * 在与服务端建立连接期间，该方法处于阻塞状态。直至连接建立成功，或者发生异常。
	 * 该start方法支持外部指定AsynchronousChannelGroup，实现多个客户端共享一组线程池资源，有效提升资源利用率。
	 *
	 * @param asynchronousChannelGroup IO事件处理线程组
	 * @return 建立连接后的会话对象
	 * @throws IOException IOException
	 * @see AsynchronousSocketChannel#connect(SocketAddress)
	 */
	public ChannelContext start(AsynchronousChannelGroup asynchronousChannelGroup) throws IOException {
		if (isCheck) {
			checkAndResetConfig();
		}
		CompletableFuture<ChannelContext> future = new CompletableFuture<>();
		start(asynchronousChannelGroup, future, new CompletionHandler<ChannelContext, CompletableFuture<ChannelContext>>() {
			@Override
			public void completed(ChannelContext channelContext, CompletableFuture<ChannelContext> future) {
				if (future.isDone() || future.isCancelled()) {
					channelContext.close();
					LOGGER.error("aio-socket version: {}; client kernel started failed because of future is done or cancelled", AioConfig.VERSION);
				} else {
					channelContext.setProtocol(clientProtocol);
					future.complete(channelContext);
					if (Objects.nonNull(heartBeat)) {
						heartMessage();
					}
					if (LOGGER.isInfoEnabled()) {
						LOGGER.info("aio-socket version: {}; client kernel started successfully", AioConfig.VERSION);
					}
				}
			}

			@Override
			public void failed(Throwable exc, CompletableFuture<ChannelContext> future) {
				future.completeExceptionally(exc);
				LOGGER.error("aio-socket version: {}; client kernel started failed", AioConfig.VERSION);
			}
		});
		try {
			if (connectTimeout > 0) {
				return future.get(connectTimeout, TimeUnit.MILLISECONDS);
			} else {
				return future.get();
			}
		} catch (Exception e) {
			future.cancel(false);
//			shutdownNow();
//			throw new IOException(e);
			LOGGER.error("aio-socket version: {}; client kernel started failed because of {}. Starting retry...", AioConfig.VERSION, e.getMessage());
			return null;
		}
	}

	/**
	 * 采用异步的方式启动客户端
	 *
	 * @param asynchronousChannelGroup 通信线程资源组
	 * @param future                   可传入回调方法中的附件对象
	 * @param handler                  异步回调
	 * @throws IOException 网络IO异常
	 */
	private void start(AsynchronousChannelGroup asynchronousChannelGroup, CompletableFuture<ChannelContext> future,
					   CompletionHandler<ChannelContext, ? super CompletableFuture<ChannelContext>> handler) throws IOException {
		AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open(asynchronousChannelGroup);
		if (this.memoryPool == null) {
			this.memoryPool = getConfig().getMemoryPoolFactory().create();
		}
		Supplier<MemoryUnit> supplier = () -> readMemoryUnitFactory.createBuffer(memoryPool.allocateBufferPage());
		if (this.config.getSocketOptions() != null) {
			for (Map.Entry<SocketOption<Object>, Object> entry : this.config.getSocketOptions().entrySet()) {
				socketChannel.setOption(entry.getKey(), entry.getValue());
			}
		}
		if (this.localAddress != null) {
			socketChannel.bind(this.localAddress);
		}
		socketChannel.connect(new InetSocketAddress(config.getHost(), config.getPort()), socketChannel, new CompletionHandler<Void, AsynchronousSocketChannel>() {
			@Override
			public void completed(Void result, AsynchronousSocketChannel socketChannel) {
				try {
					AsynchronousSocketChannel connectedChannel = socketChannel;
					if (config.getMonitor() != null) {
						connectedChannel = config.getMonitor().shouldAccept(socketChannel);
					}
					if (connectedChannel == null) {
						throw new RuntimeException("NetMonitor refuse channel");
					}
					//连接成功则构造AIOChannelContext对象
					channelContext = new TCPChannelContext(connectedChannel, config, new ReadCompletionHandler(), new WriteCompletionHandler(), memoryPool.allocateBufferPage());
					channelContext.initTCPChannelContext(supplier);
					handler.completed(channelContext, future);
				} catch (Exception e) {
					failed(e, socketChannel);
				}
			}

			@Override
			public void failed(Throwable exc, AsynchronousSocketChannel socketChannel) {
				try {
					handler.failed(exc, future);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (socketChannel != null) {
						AIOUtil.close(socketChannel);
					}
//					shutdownNow();
				}
			}
		});
	}

	/**
	 * 检查配置项
	 */
	private void checkAndResetConfig() {
		this.isCheck = false;
		getConfig().initMemoryPoolFactory();
		Plugins plugins = getConfig().getPlugins();
		getConfig().setMonitor(plugins)
				.setHandler(plugins);
	}

	/**
	 * 心跳
	 */
	private void heartMessage() {
		TimerService.getInstance().schedule(() -> {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("aio-socket version: {}; client kernel are sending heartbeat", AioConfig.VERSION);
			}
			Aio.send(this.channelContext, this.heartBeat);
			heartMessage();
		}, 5, TimeUnit.SECONDS);
	}

	/**
	 * 停止客户端服务.
	 * 调用该方法会触发AioSession的close方法
	 * 并且如果当前客户端若是通过执行{@link ClientBootstrap#start()}方法构建的
	 * 同时会触发asynchronousChannelGroup的shutdown动作。
	 */
	public final void shutdown() {
		shutdown0(false);
	}

	/**
	 * 立即关闭客户端
	 */
	public final void shutdownNow() {
		shutdown0(true);
	}

	/**
	 * 停止client
	 *
	 * @param flag 是否立即停止
	 */
	private void shutdown0(boolean flag) {
		if (this.channelContext != null) {
			this.channelContext.close(flag);
			this.channelContext = null;
		}
		if (this.asynchronousChannelGroup != null) {
			this.asynchronousChannelGroup.shutdown();
			this.asynchronousChannelGroup = null;
		}
	}

	/**
	 * 设置线程数
	 *
	 * @param threadNum 线程数
	 * @return this
	 */
	public ClientBootstrap setThreadNum(int threadNum) {
		getConfig().setBossThreadNumber(threadNum);
		return this;
	}

	/**
	 * 获取配置信息
	 *
	 * @return AioConfig
	 */
	public AioConfig getConfig() {
		return this.config;
	}

	/**
	 * 重连访问项
	 *
	 * @param isCheck bool
	 */
	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	/**
	 * 设置内存池工厂
	 *
	 * @param size 内存页大小
	 * @param num 内存页个数
	 * @param useDirect 是否开启堆外内存
	 * @return this
	 */
	public ClientBootstrap setBufferFactory(int size, int num, boolean useDirect) {
		getConfig().setDirect(useDirect).setMemoryBlockSize(size).setMemoryBlockNum(num);
		return this;
	}

	/**
	 * 设置写缓冲区大小
	 *
	 * @param writeBufferSize 写缓冲区大小
	 * @param maxWaitNum      最大等待队列长度
	 * @return this
	 */
	public ClientBootstrap setWriteBufferSize(int writeBufferSize, int maxWaitNum) {
		getConfig().setWriteBufferSize(writeBufferSize)
				.setMaxWaitNum(maxWaitNum);
		return this;
	}

	/**
	 * 设置读缓冲区大小
	 *
	 * @param readBufferSize 读缓冲区大小
	 * @return this
	 */
	public ClientBootstrap setReadBufferSize(int readBufferSize) {
		getConfig().setReadBufferSize(readBufferSize);
		return this;
	}

	/**
	 * 注册插件
	 *
	 * @param plugin 插件项
	 * @return this
	 */
	public ClientBootstrap addPlugin(Plugin plugin) {
		getConfig().getPlugins().addPlugin(plugin);
		return this;
	}

	public ClientBootstrap addHeartPacket(Packet heartPacket) {
		this.heartBeat = heartPacket;
		return this;
	}

	/**
	 * 绑定指定端口
	 *
	 * @param port 端口号
	 * @return this
	 */
	public ClientBootstrap setLocalSocketAddress(int port) {
		this.localAddress = new InetSocketAddress(port);
		return this;
	}
}
