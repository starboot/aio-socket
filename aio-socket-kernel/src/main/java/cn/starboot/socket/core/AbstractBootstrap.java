package cn.starboot.socket.core;

import cn.starboot.socket.Monitor;
import cn.starboot.socket.enums.StateMachineEnum;
import cn.starboot.socket.jdk.aio.ImproveAsynchronousChannelGroup;
import cn.starboot.socket.jdk.aio.ImproveAsynchronousSocketChannel;
import cn.starboot.socket.utils.ThreadUtils;
import cn.starboot.socket.utils.pool.memory.MemoryPool;
import cn.starboot.socket.utils.pool.memory.MemoryUnitFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

abstract class AbstractBootstrap {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBootstrap.class);

	/**
	 * 内存池
	 */
	private MemoryPool memoryPool;

	/**
	 * 内核IO线程池
	 */
	private ExecutorService bossExecutorService;

	/**
	 * 服务器配置类
	 */
	private final AioConfig config;

	/**
	 * 读完成处理类
	 */
	private final CompletionHandler<Integer, TCPChannelContext> aioReadCompletionHandler;

	/**
	 * 写完成处理类
	 */
	private final CompletionHandler<Integer, TCPChannelContext> aioWriteCompletionHandler;

	/**
	 * AIO 通道组
	 */
	private ImproveAsynchronousChannelGroup asynchronousChannelGroup;

	/**
	 * 虚拟内存工厂，这里为读操作获取虚拟内存
	 */
	private final MemoryUnitFactory readMemoryUnitFactory
			= memoryBlock -> memoryBlock.allocate(getConfig().getReadBufferSize());

	/**
	 * 启动
	 */
	protected void beforeStart() throws IOException {
		startExecutorService();
		getConfig().initMemoryPoolFactory();
		if (this.memoryPool == null) {
			this.memoryPool = getConfig().getMemoryPoolFactory().create();
		}
		if (this.asynchronousChannelGroup == null) {
			this.asynchronousChannelGroup =
					ImproveAsynchronousChannelGroup
							.withCachedThreadPool(this.bossExecutorService,
									getConfig().getBossThreadNumber());
		}

	}

	/**
	 * socketChannel 和 ChannelContext联系
	 */
	private final Function<ImproveAsynchronousSocketChannel, TCPChannelContext> bootstrapFunction
			= (improveAsynchronousSocketChannel) ->
			new TCPChannelContext(improveAsynchronousSocketChannel,
					getConfig(),
					getAioReadCompletionHandler(),
					getAioWriteCompletionHandler(),
					memoryPool.allocateBufferPage(),
					readMemoryUnitFactory);

	protected AbstractBootstrap(AioConfig config) {
		this.config = config;
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
			 * @param exc            异常信息
			 * @param channelContext 读完成出错的通道
			 */
			@Override
			public void failed(Throwable exc, TCPChannelContext channelContext) {
				try {
					channelContext.getAioConfig().getHandler().stateEvent(channelContext, StateMachineEnum.INPUT_EXCEPTION, exc);
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					channelContext.close(false);
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
			public void failed(Throwable exc, TCPChannelContext channelContext) {
				try {
					channelContext.getAioConfig().getHandler().stateEvent(channelContext, StateMachineEnum.ENCODE_EXCEPTION, exc);
					channelContext.close(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}

	public AioConfig getConfig() {
		return this.config;
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

	/**
	 * 启动内核线程池
	 */
	private void startExecutorService() {
		if (getConfig().getBossThreadNumber() > 0) {
			this.bossExecutorService = ThreadUtils.getGroupExecutor(getConfig().getBossThreadNumber());
		} else {
			this.bossExecutorService = ThreadUtils.getGroupExecutor();
		}
//        if (getConfig().getWorkerThreadNumber() > 0) {
//            this.workerExecutorService = ThreadUtils.getAioExecutor(getConfig().getWorkerThreadNumber());
//        }else {
//            this.workerExecutorService = ThreadUtils.getAioExecutor();
//        }
	}

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
		if (this.memoryPool != null) {
			this.memoryPool.release();
		}
	}
}
