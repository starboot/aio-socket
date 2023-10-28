package cn.starboot.socket.core.tcp;

import cn.starboot.socket.core.AbstractBootstrap;
import cn.starboot.socket.core.AioConfig;
import cn.starboot.socket.core.Monitor;
import cn.starboot.socket.core.enums.StateMachineEnum;
import cn.starboot.socket.core.jdk.aio.ImproveAsynchronousChannelGroup;
import cn.starboot.socket.core.jdk.aio.ImproveAsynchronousSocketChannel;
import cn.starboot.socket.core.spi.KernelBootstrapProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

abstract class TCPBootstrap extends AbstractBootstrap {

	private static final Logger LOGGER = LoggerFactory.getLogger(TCPBootstrap.class);

	private final KernelBootstrapProvider kernelBootstrapProvider;

	private final TCPKernelBootstrapProvider tcpKernelBootstrapProvider;

	/**
	 * AIO 通道组
	 */
	private ImproveAsynchronousChannelGroup asynchronousChannelGroup;

	/**
	 * 读完成处理类
	 */
	private final CompletionHandler<Integer, TCPChannelContext> aioReadCompletionHandler;

	/**
	 * 写完成处理类
	 */
	private final CompletionHandler<Integer, TCPChannelContext> aioWriteCompletionHandler;

	/**
	 * socketChannel 和 ChannelContext联系
	 */
	private final Function<ImproveAsynchronousSocketChannel, TCPChannelContext> bootstrapFunction
			= (improveAsynchronousSocketChannel) -> new TCPChannelContext(
					improveAsynchronousSocketChannel,
					getConfig(),
					getAioReadCompletionHandler(),
					getAioWriteCompletionHandler(),
					memoryPool.allocateMemoryBlock(),
					readMemoryUnitFactory);

	@Override
	public final KernelBootstrapProvider KernelProvider() {
		return kernelBootstrapProvider;
	}

	public final TCPKernelBootstrapProvider TcpProvider() {
		return tcpKernelBootstrapProvider;
	}

	protected void beforeStart() throws IOException {
		super.beforeStart();
		if (this.asynchronousChannelGroup == null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Start the default thread pool for the aio-socket kernel with thread pool number :{} ",
						getConfig().getBossThreadNumber());
			}
			startExecutorService();
			this.asynchronousChannelGroup =
					ImproveAsynchronousChannelGroup
							.withCachedThreadPool(this.bossExecutorService,
									getConfig().getBossThreadNumber());
		}

	}


	TCPBootstrap(AioConfig config,
				 TCPKernelBootstrapProvider tcpKernelBootstrapProvider,
				 KernelBootstrapProvider kernelBootstrapProvider) {
		super(config);
		this.tcpKernelBootstrapProvider = tcpKernelBootstrapProvider;
		this.kernelBootstrapProvider = kernelBootstrapProvider;
		this.aioReadCompletionHandler = new CompletionHandler<Integer, TCPChannelContext>() {
			@Override
			public void completed(Integer result, TCPChannelContext channelContext) {
				// 读取完成,result:实际读取的字节数。如果对方关闭连接则result=-1。
				try {
					// 接收到的消息进行预处理
					Monitor monitor = channelContext.getAioConfig().getMonitor();
					if (monitor != null) {
						monitor.afterRead(channelContext, result);
					}
					//触发读回调
					channelContext.signalRead(result == -1);
				} catch (Exception e) {
					failed(e, channelContext);
				}
			}

			/**
			 * 读失败，他会在什么时候触发呢？（忽略completed方法中异常后的调用）
			 * 1. 会在客户端机器主动杀死aio-socket客户端进程或者客户端机器突然宕机或坏掉时，则服务器端对应的ChannelContext会调用此方法
			 * 2. 相比之下，当客户端的ChannelContext正在读通道时，服务器关闭了对应的连接，则客户端的ChannelContext会调用此方法
			 * @param throwable      异常信息
			 * @param channelContext 读完成出错的通道
			 */
			@Override
			public void failed(Throwable throwable, TCPChannelContext channelContext) {
				try {
					channelContext
							.getAioConfig()
							.getHandler()
							.stateEvent(channelContext, StateMachineEnum.INPUT_EXCEPTION, throwable);
					channelContext.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		this.aioWriteCompletionHandler = new CompletionHandler<Integer, TCPChannelContext>() {
			@Override
			public void completed(Integer result, TCPChannelContext channelContext) {
				try {
					Monitor monitor = channelContext.getAioConfig().getMonitor();
					if (monitor != null) {
						monitor.afterWrite(channelContext, result);
					}
					channelContext.writeCompleted();
				} catch (Exception e) {
					failed(e, channelContext);
				}
			}

			@Override
			public void failed(Throwable throwable, TCPChannelContext channelContext) {
				try {
					channelContext
							.getAioConfig()
							.getHandler()
							.stateEvent(channelContext, StateMachineEnum.ENCODE_EXCEPTION, throwable);
					channelContext.close(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}


	protected CompletionHandler<Integer, TCPChannelContext> getAioReadCompletionHandler() {
		return aioReadCompletionHandler;
	}

	protected CompletionHandler<Integer, TCPChannelContext> getAioWriteCompletionHandler() {
		return aioWriteCompletionHandler;
	}

	protected Function<ImproveAsynchronousSocketChannel, TCPChannelContext> getBootstrapFunction() {
		return bootstrapFunction;
	}

	protected void setAsynchronousChannelGroup(ImproveAsynchronousChannelGroup asynchronousChannelGroup) {
		this.asynchronousChannelGroup = asynchronousChannelGroup;
	}

	protected ImproveAsynchronousChannelGroup getAsynchronousChannelGroup() {
		return asynchronousChannelGroup;
	}


	@Override
	public void shutdown() {
		if (!this.asynchronousChannelGroup.isTerminated()) {
			try {
				this.asynchronousChannelGroup.shutdownNow();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			this.asynchronousChannelGroup.awaitTermination(3, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		super.shutdown();
	}
}
