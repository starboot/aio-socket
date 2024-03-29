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
package cn.starboot.socket.core.tcp;

import cn.starboot.socket.core.*;
import cn.starboot.socket.core.enums.ProtocolEnum;
import cn.starboot.socket.core.config.AioClientConfig;
import cn.starboot.socket.core.exception.AioParameterException;
import cn.starboot.socket.core.jdk.aio.ImproveAsynchronousChannelGroup;
import cn.starboot.socket.core.jdk.aio.ImproveAsynchronousSocketChannel;
import cn.starboot.socket.core.intf.AioHandler;
import cn.starboot.socket.core.plugins.Plugin;
import cn.starboot.socket.core.plugins.Plugins;
import cn.starboot.socket.core.spi.KernelBootstrapProvider;
import cn.starboot.socket.core.utils.TimerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.channels.CompletionHandler;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * AIO Client 启动类
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
final class TCPClientBootstrap extends TCPBootstrap implements ClientBootstrap {

	private static final Logger LOGGER = LoggerFactory.getLogger(TCPClientBootstrap.class);

	/**
	 * 重连插件使用
	 */
	private boolean isInit = true;

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
	private ProtocolEnum clientProtocol;

	/**
	 * 网络连接的会话对象
	 */
	private TCPChannelContext channelContext;

	/**
	 * 连接超时时间
	 */
	private final static int connectTimeout = 5000;


	/**
	 * 当前构造方法设置了启动Aio客户端的必要参数，基本实现开箱即用。
	 *
	 */
	TCPClientBootstrap(TCPKernelBootstrapProvider tcpKernelBootstrapProvider, KernelBootstrapProvider kernelBootstrapProvider) {
		super(new AioClientConfig(), tcpKernelBootstrapProvider, kernelBootstrapProvider);
	}

	/**
	 * 启动客户端。
	 * 本方法会构建线程数为threadNum的{@code asynchronousChannelGroup}
	 * 并通过调用{@link TCPClientBootstrap#start(ImproveAsynchronousChannelGroup)}启动服务。
	 *
	 * @return 建立连接后的会话对象
	 * @throws IOException IOException
	 * @see TCPClientBootstrap#start(ImproveAsynchronousChannelGroup)
	 */
	@Override
	public final ChannelContext start() throws IOException {

//		this.asynchronousChannelGroup = ImproveAsynchronousChannelGroup.withCachedThreadPool(ThreadUtils.getGroupExecutor(getConfig().getBossThreadNumber()), getConfig().getBossThreadNumber());
		return start(null);
	}

	/**
	 * 启动客户端。
	 * 在与服务端建立连接期间，该方法处于阻塞状态。直至连接建立成功，或者发生异常。
	 * 该start方法支持外部指定AsynchronousChannelGroup，实现多个客户端共享一组线程池资源，有效提升资源利用率。
	 *
	 * @param asynchronousChannelGroup IO事件处理线程组
	 * @return 建立连接后的会话对象
	 * @throws IOException IOException
	 * @see java.nio.channels.AsynchronousSocketChannel#connect(SocketAddress)
	 */
	@Override
	public ChannelContext start(ImproveAsynchronousChannelGroup asynchronousChannelGroup) throws IOException {
		setAsynchronousChannelGroup(asynchronousChannelGroup);
		if (isInit) {
			checkAndResetConfig();
		}
		CompletableFuture<ChannelContext> future = new CompletableFuture<>();
		start(future, new CompletionHandler<ChannelContext, CompletableFuture<ChannelContext>>() {
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
			return future.get(connectTimeout, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			future.cancel(false);
			shutdownNow();
//			throw new IOException(e);
			LOGGER.error("aio-socket version: {}; client kernel started failed because of {}. Starting retry...", AioConfig.VERSION, e.getMessage());
			return null;
		}
	}


	/**
	 * 采用异步的方式启动客户端
	 *
	 * @param future                   可传入回调方法中的附件对象
	 * @param handler                  异步回调
	 * @throws IOException 网络IO异常
	 */
	private void start(CompletableFuture<ChannelContext> future,
					   CompletionHandler<ChannelContext, ? super CompletableFuture<ChannelContext>> handler)
			throws IOException {

		beforeStart();
		ImproveAsynchronousSocketChannel socketChannel = ImproveAsynchronousSocketChannel.open(getAsynchronousChannelGroup());
//		Supplier<MemoryUnit> supplier = () -> readMemoryUnitFactory.createBuffer(memoryPool.allocateBufferPage());
		if (getConfig().getSocketOptions() != null) {
			for (Map.Entry<SocketOption<Object>, Object> entry : getConfig().getSocketOptions().entrySet()) {
				socketChannel.setOption(entry.getKey(), entry.getValue());
			}
		}
		if (this.localAddress != null) {
			socketChannel.bind(this.localAddress);
		}
		socketChannel.connect(new InetSocketAddress(getConfig().getHost(), getConfig().getPort()), socketChannel, new CompletionHandler<Void, ImproveAsynchronousSocketChannel>() {
			@Override
			public void completed(Void result, ImproveAsynchronousSocketChannel socketChannel) {
				try {
					ImproveAsynchronousSocketChannel connectedChannel = socketChannel;
					if (getConfig().getMonitor() != null) {
						connectedChannel = getConfig().getMonitor().agreeAccept(socketChannel);
					}
					if (connectedChannel == null) {
						throw new RuntimeException("NetMonitor refuse channel");
					}
					//连接成功则构造AIOChannelContext对象
					channelContext = getBootstrapFunction().apply(connectedChannel);
					channelContext.initTCPChannelContext();
					handler.completed(channelContext, future);
				} catch (Exception e) {
					failed(e, socketChannel);
				}
			}

			@Override
			public void failed(Throwable throwable, ImproveAsynchronousSocketChannel socketChannel) {
				handler.failed(throwable, future);
				TCPKernelUtils.closeImproveAsynchronousSocketChannel(socketChannel);
				shutdownNow();
			}
		});
	}

	/**
	 * 检查配置项
	 */
	private void checkAndResetConfig() {
		this.isInit = false;
//		getConfig().initMemoryPoolFactory();
		Plugins plugins = getConfig().getPlugins();
		getConfig().setMonitor(plugins).setHandler(plugins);
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
	 * 并且如果当前客户端若是通过执行{@link TCPClientBootstrap#start()}方法构建的
	 * 同时会触发asynchronousChannelGroup的shutdown动作。
	 */
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

	/**
	 * 绑定指定端口
	 *
	 * @param port 端口号
	 * @return this
	 */
	@Override
	public ClientBootstrap listen(int port) {
		this.localAddress = new InetSocketAddress(port);
		return this;
	}

	/**
	 *
	 * @param host    远程服务器地址
	 * @param port    远程服务器端口号
	 * @return this
	 */
	@Override
	public ClientBootstrap remote(String host, int port) {
		getConfig().setHost(host);
		getConfig().setPort(port);
		return this;
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
		super.shutdown();
	}

	/**
	 * 设置线程数
	 * 不懂通讯的同学就别设置线程数，容易产生死锁
	 * 客户端默认两个线程，刚刚好
	 *
	 * @param threadNum 线程数
	 * @return this
	 */
	@Override
	public ClientBootstrap setThreadNum(int threadNum) {
		try {
			getConfig().setKernelThreadNumber(threadNum);
		} catch (AioParameterException e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * 设置内存池工厂
	 *
	 * @param size 内存页大小
	 * @param num 内存页个数
	 * @param useDirect 是否开启堆外内存
	 * @return this
	 */
	@Override
	public ClientBootstrap setMemoryPoolFactory(int size, int num, boolean useDirect) {
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
	@Override
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
	@Override
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

	@Override
	public ClientBootstrap setMemoryKeep(boolean isMemoryKeep) {
		getConfig().setMemoryKeep(isMemoryKeep);
		return this;
	}

	@Override
	public ClientBootstrap addHeartPacket(Packet heartPacket) {
		this.heartBeat = heartPacket;
		return this;
	}

}
