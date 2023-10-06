package cn.starboot.socket.jdk.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class NioEventLoopWorker implements Runnable {

	/**
	 * group运行状态
	 */
	private boolean running = true;

	/**
	 * 当前Worker绑定的Selector
	 */
	final Selector selector;
	private final Consumer<SelectionKey> consumer;
	private final ConcurrentLinkedQueue<Consumer<Selector>> consumers = new ConcurrentLinkedQueue<>();
	private Thread workerThread;

	public NioEventLoopWorker(Selector selector, Consumer<SelectionKey> consumer) {
		this.selector = selector;
		this.consumer = consumer;
	}

	public void shutdown() {
		this.running = false;
	}

	public Thread getWorkerThread() {
		return workerThread;
	}

	public Selector getSelector() {
		return selector;
	}

	/**
	 * 注册事件
	 */
	public void addRegister(Consumer<Selector> register) {
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
