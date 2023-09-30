package cn.starboot.socket.jdk.aio.impl;

import cn.starboot.socket.jdk.aio.ImproveAsynchronousChannelGroup;
import cn.starboot.socket.jdk.aio.ImproveAsynchronousChannelProvider;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

final class ImproveAsynchronousChannelGroupImpl extends ImproveAsynchronousChannelGroup {

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
	private final Worker[] commonWorkers;
	/**
	 * read工作组
	 */
	private final Worker[] readWorkers;
	/**
	 * 线程池分配索引
	 */
	private final AtomicInteger readIndex = new AtomicInteger(0);
	private final AtomicInteger commonIndex = new AtomicInteger(0);

	/**
	 * group运行状态
	 */
	private boolean running = true;

	/**
	 * Initialize a new instance of this class.
	 *
	 * @param provider The asynchronous channel provider for this group
	 */
	protected ImproveAsynchronousChannelGroupImpl(ImproveAsynchronousChannelProvider provider, ExecutorService executorService, int threadNum) throws IOException {
		super(provider);
		//init threadPool for read
		this.readExecutorService = executorService;
		this.readWorkers = new Worker[threadNum];
		for (int i = 0; i < threadNum; i++) {
			readWorkers[i] = new Worker(Selector.open(), selectionKey -> {
				ImproveAsynchronousSocketChannelImpl asynchronousSocketChannel = (ImproveAsynchronousSocketChannelImpl) selectionKey.attachment();
				asynchronousSocketChannel.doRead();
			});
			this.readExecutorService.execute(readWorkers[i]);
		}

		//init threadPool for write and connect
		final int commonThreadNum = 1;
		commonExecutorService = getSingleThreadExecutor("aio-socket:common");
		this.commonWorkers = new Worker[commonThreadNum];

		for (int i = 0; i < commonThreadNum; i++) {
			commonWorkers[i] = new Worker(Selector.open(), selectionKey -> {
				if (selectionKey.isWritable()) {
					ImproveAsynchronousSocketChannelImpl asynchronousSocketChannel = (ImproveAsynchronousSocketChannelImpl) selectionKey.attachment();
					//直接调用interestOps的效果比 removeOps(selectionKey, SelectionKey.OP_WRITE) 更好
					selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
					while (asynchronousSocketChannel.doWrite()) ;
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

	public Worker getReadWorker() {
		return readWorkers[(readIndex.getAndIncrement() & Integer.MAX_VALUE) % readWorkers.length];
	}

	public Worker getCommonWorker() {
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
		running = false;
		readExecutorService.shutdown();
		commonExecutorService.shutdown();
	}

	@Override
	public void shutdownNow() throws IOException {
		running = false;
		readExecutorService.shutdownNow();
		commonExecutorService.shutdownNow();
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return readExecutorService.awaitTermination(timeout, unit);
	}

	public static void interestOps(Worker worker, SelectionKey selectionKey, int opt) {
		if ((selectionKey.interestOps() & opt) != 0) {
			return;
		}
		selectionKey.interestOps(selectionKey.interestOps() | opt);
		//Worker线程无需wakeup
		if (worker.workerThread != Thread.currentThread()) {
			selectionKey.selector().wakeup();
		}
	}

	class Worker implements Runnable {
		/**
		 * 当前Worker绑定的Selector
		 */
		final Selector selector;
		private final Consumer<SelectionKey> consumer;
		private final ConcurrentLinkedQueue<Consumer<Selector>> consumers = new ConcurrentLinkedQueue<>();
		private Thread workerThread;

		Worker(Selector selector, Consumer<SelectionKey> consumer) {
			this.selector = selector;
			this.consumer = consumer;
		}

		/**
		 * 注册事件
		 */
		final void addRegister(Consumer<Selector> register) {
			consumers.offer(register);
			selector.wakeup();
		}

		@Override
		public final void run() {
			workerThread = Thread.currentThread();
			// 优先获取SelectionKey,若无关注事件触发则阻塞在selector.select(),减少select被调用次数
			Set<SelectionKey> keySet = selector.selectedKeys();
			try {
				while (running) {
					Consumer<Selector> selectorConsumer;
					while ((selectorConsumer = consumers.poll()) != null) {
						selectorConsumer.accept(selector);
					}
					selector.select();

					// 执行本次已触发待处理的事件
					for (SelectionKey key : keySet) {
						consumer.accept(key);
					}
					keySet.clear();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					selector.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
