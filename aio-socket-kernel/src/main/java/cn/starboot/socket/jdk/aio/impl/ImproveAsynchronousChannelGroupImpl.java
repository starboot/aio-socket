package cn.starboot.socket.jdk.aio.impl;

import cn.starboot.socket.jdk.aio.ImproveAsynchronousChannelGroup;
import cn.starboot.socket.jdk.aio.ImproveAsynchronousChannelProvider;
import cn.starboot.socket.jdk.nio.ImproveNioSelector;
import cn.starboot.socket.jdk.nio.NioEventLoopWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

final class ImproveAsynchronousChannelGroupImpl extends ImproveAsynchronousChannelGroup {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImproveAsynchronousChannelGroupImpl.class);

	/**
	 * 递归回调次数上限
	 */
	public static final int MAX_INVOKER = 8;

	/**
	 * 读回调处理线程池,可用于业务处理
	 */
	private final ExecutorService readExecutorService;

	/**
	 * 写回调线程池
	 */
	private final ExecutorService commonExecutorService;

	/**
	 * write工作组
	 */
	private final NioEventLoopWorker[] commonWorkers;

	/**
	 * read工作组
	 */
	private final NioEventLoopWorker[] readWorkers;

	/**
	 * 线程池分配索引
	 */
	private final AtomicInteger readIndex = new AtomicInteger(0);

	private final AtomicInteger commonIndex = new AtomicInteger(0);

	/**
	 * Initialize a new instance of this class.
	 *
	 * @param provider The asynchronous channel provider for this group
	 */
	protected ImproveAsynchronousChannelGroupImpl(ImproveAsynchronousChannelProvider provider, ExecutorService executorService, int threadNum) throws IOException {
		super(provider);
		//init threadPool for read
		this.readExecutorService = executorService;
		this.readWorkers = new NioEventLoopWorker[threadNum];
		for (int i = 0; i < threadNum; i++) {
			readWorkers[i] = new NioEventLoopWorker(ImproveNioSelector.open(), selectionKey -> {
				ImproveAsynchronousSocketChannelImpl asynchronousSocketChannel = (ImproveAsynchronousSocketChannelImpl) selectionKey.attachment();
				asynchronousSocketChannel.doRead();
			});
			this.readExecutorService.execute(readWorkers[i]);
		}

		//init threadPool for write and connect
		final int commonThreadNum = 1;
		commonExecutorService = getSingleThreadExecutor("aio-socket:common");
		this.commonWorkers = new NioEventLoopWorker[commonThreadNum];

		for (int i = 0; i < commonThreadNum; i++) {
			commonWorkers[i] = new NioEventLoopWorker(ImproveNioSelector.open(), selectionKey -> {
				if (selectionKey.isWritable()) {
					ImproveAsynchronousSocketChannelImpl asynchronousSocketChannel = (ImproveAsynchronousSocketChannelImpl) selectionKey.attachment();
					//直接调用interestOps的效果比 removeOps(selectionKey, SelectionKey.OP_WRITE) 更好
					selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
					while (asynchronousSocketChannel.doWrite());
				} else if (selectionKey.isAcceptable()) {
					ImproveAsynchronousServerSocketChannelImpl serverSocketChannel = (ImproveAsynchronousServerSocketChannelImpl) selectionKey.attachment();
					serverSocketChannel.doAccept();
				} else if (selectionKey.isConnectable()) {
					Runnable runnable = (Runnable) selectionKey.attachment();
					runnable.run();
				} else if (selectionKey.isReadable()) {
					//仅同步read会用到此线程资源
					ImproveAsynchronousSocketChannelImpl asynchronousSocketChannel = (ImproveAsynchronousSocketChannelImpl) selectionKey.attachment();
					removeOps(selectionKey, SelectionKey.OP_READ);
					asynchronousSocketChannel.doRead();
				}
			});
			commonExecutorService.execute(commonWorkers[i]);
		}
	}

	private ThreadPoolExecutor getSingleThreadExecutor(final String prefix) {
		return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<>(), r -> new Thread(r, prefix));
	}

	/**
	 * 移除关注事件
	 *
	 * @param selectionKey 待操作的selectionKey
	 * @param opt          移除的事件
	 */
	public static void removeOps(SelectionKey selectionKey, int opt) {
		if ((selectionKey.interestOps() & opt) != 0) {
			selectionKey.interestOps(selectionKey.interestOps() & ~opt);
		}
	}

	public NioEventLoopWorker getReadWorker() {
		return readWorkers[(readIndex.getAndIncrement() & Integer.MAX_VALUE) % readWorkers.length];
	}

	public NioEventLoopWorker getCommonWorker() {
		return commonWorkers[(commonIndex.getAndIncrement() & Integer.MAX_VALUE) % commonWorkers.length];
	}

	@Override
	public boolean isShutdown() {
		return readExecutorService.isShutdown();
	}

	@Override
	public boolean isTerminated() {
		return readExecutorService.isTerminated();
	}

	@Override
	public void shutdown() {
		closeAllWorker();
		readExecutorService.shutdown();
		commonExecutorService.shutdown();
	}

	@Override
	public void shutdownNow() throws IOException {
		closeAllWorker();
		readExecutorService.shutdownNow();
		commonExecutorService.shutdownNow();
	}

	private void closeAllWorker() {
		for (NioEventLoopWorker readWorker : readWorkers) {
			readWorker.shutdown();
		}
		for (NioEventLoopWorker commonWorker : commonWorkers) {
			commonWorker.shutdown();
		}
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return readExecutorService.awaitTermination(timeout, unit);
	}

	public static void interestOps(NioEventLoopWorker worker, SelectionKey selectionKey, int opt) {
		if ((selectionKey.interestOps() & opt) != 0) {
			return;
		}
		selectionKey.interestOps(selectionKey.interestOps() | opt);
		//Worker线程无需wakeup
		if (worker.getWorkerThread() != Thread.currentThread()) {
			selectionKey.selector().wakeup();
		}
	}

}
