package cn.starboot.socket.core.jdk.aio.impl;

import cn.starboot.socket.core.jdk.aio.ImproveAsynchronousChannelGroup;
import cn.starboot.socket.core.jdk.aio.ImproveAsynchronousChannelProvider;
import cn.starboot.socket.core.jdk.nio.ImproveNioSelector;
import cn.starboot.socket.core.jdk.nio.NioEventLoopWorker;
import cn.starboot.socket.core.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private final ExecutorService subReactorExecutorService;

	/**
	 * 写回调线程池
	 */
	private final ExecutorService mainReactorExecutorService;

	/**
	 * write工作组
	 */
	private final NioEventLoopWorker[] mainReactor;

	/**
	 * read工作组
	 */
	private final NioEventLoopWorker[] subReactor;

	/**
	 * 线程池分配索引
	 */
	private final AtomicInteger mainReactorIndex = new AtomicInteger(0);

	private final AtomicInteger subReactorIndex = new AtomicInteger(0);

	/**
	 * Initialize a new instance of this class.
	 *
	 * @param provider The asynchronous channel provider for this group
	 */
	protected ImproveAsynchronousChannelGroupImpl(ImproveAsynchronousChannelProvider provider, ExecutorService executorService, int threadNum) {
		super(provider);

		final int commonThreadNum = 1;
		this.mainReactorExecutorService = ThreadUtils.getGroupExecutor(commonThreadNum);
		this.mainReactor = new NioEventLoopWorker[commonThreadNum];
		initMainReactor(commonThreadNum);

		this.subReactorExecutorService = executorService;
		this.subReactor = new NioEventLoopWorker[threadNum];
		initSubReactor(threadNum);

	}

	private void initSubReactor(int threadNum) {
		for (int i = 0; i < threadNum; i++) {
			subReactor[i] = new NioEventLoopWorker(ImproveNioSelector.open(),
					selectionKey -> {
						ImproveAsynchronousSocketChannelImpl asynchronousSocketChannel = (ImproveAsynchronousSocketChannelImpl) selectionKey.attachment();
						asynchronousSocketChannel.doRead();
					});
			this.subReactorExecutorService.execute(subReactor[i]);
		}
	}

	private void initMainReactor(int threadNum) {
		for (int i = 0; i < threadNum; i++) {
			mainReactor[i] = new NioEventLoopWorker(ImproveNioSelector.open(),
					selectionKey -> {
						if (selectionKey.isWritable()) {
							ImproveAsynchronousSocketChannelImpl asynchronousSocketChannel =
									(ImproveAsynchronousSocketChannelImpl) selectionKey.attachment();
							selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
							while (asynchronousSocketChannel.doWrite()) ;
						} else if (selectionKey.isAcceptable()) {
							ImproveAsynchronousServerSocketChannelImpl serverSocketChannel =
									(ImproveAsynchronousServerSocketChannelImpl) selectionKey.attachment();
							serverSocketChannel.doAccept();
						} else if (selectionKey.isConnectable()) {
							Runnable runnable = (Runnable) selectionKey.attachment();
							runnable.run();
						} else if (selectionKey.isReadable()) {
							//同步read
							ImproveAsynchronousSocketChannelImpl asynchronousSocketChannel =
									(ImproveAsynchronousSocketChannelImpl) selectionKey.attachment();
							removeOps(selectionKey, SelectionKey.OP_READ);
							asynchronousSocketChannel.doRead();
						}
					});
			mainReactorExecutorService.execute(mainReactor[i]);
		}
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

	public NioEventLoopWorker getSubReactor() {
		return subReactor[(mainReactorIndex.getAndIncrement() & Integer.MAX_VALUE) % subReactor.length];
	}

	public NioEventLoopWorker getMainReactor() {
		return mainReactor[(subReactorIndex.getAndIncrement() & Integer.MAX_VALUE) % mainReactor.length];
	}

	@Override
	public boolean isShutdown() {
		return subReactorExecutorService.isShutdown() && mainReactorExecutorService.isShutdown();
	}

	@Override
	public boolean isTerminated() {
		return subReactorExecutorService.isTerminated() && mainReactorExecutorService.isTerminated();
	}

	@Override
	public void shutdown() {
		closeAllReactor();
		subReactorExecutorService.shutdown();
		mainReactorExecutorService.shutdown();
	}

	@Override
	public void shutdownNow() {
		closeAllReactor();
		subReactorExecutorService.shutdownNow();
		mainReactorExecutorService.shutdownNow();
	}

	private void closeAllReactor() {
		for (NioEventLoopWorker reactor : subReactor) {
			reactor.shutdown();
		}
		for (NioEventLoopWorker reactor : mainReactor) {
			reactor.shutdown();
		}
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		closeAllReactor();
		return subReactorExecutorService.awaitTermination(timeout, unit)
				&& mainReactorExecutorService.awaitTermination(timeout, unit);
	}

	public static void interestOps(NioEventLoopWorker worker, SelectionKey selectionKey, int opt) {
		if ((selectionKey.interestOps() & opt) != 0) {
			return;
		}
		selectionKey.interestOps(selectionKey.interestOps() | opt);
		//Worker线程无需wakeup
		if (worker.getNioEventLoopThread() != Thread.currentThread()) {
			selectionKey.selector().wakeup();
		}
	}

}
