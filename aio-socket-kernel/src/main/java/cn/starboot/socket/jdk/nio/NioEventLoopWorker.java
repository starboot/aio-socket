package cn.starboot.socket.jdk.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * nio轮训工作者
 * @author MDong
 */
public class NioEventLoopWorker implements Runnable {

	/**
	 * 当前NioEventLoopWorker绑定的Selector
	 */
	private final Selector selector;

	/**
	 * 当前worker所属线程
	 */
	private Thread workerThread;

	/**
	 * group运行状态
	 */
	private boolean running = true;

	/**
	 * 用于处理轮训结果的声明式函数
	 */
	private final Consumer<SelectionKey> consumer;

	/**
	 * 待注册的事件
	 */
	private final ConcurrentLinkedQueue<Consumer<Selector>> consumers = new ConcurrentLinkedQueue<>();

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
		// 若无关注事件触发则阻塞在select(),减少select被调用次数
		Set<SelectionKey> keySet = selector.selectedKeys();
		try {
			while (running) {
				Consumer<Selector> selectorConsumer;
				while ((selectorConsumer = consumers.poll()) != null) {
					selectorConsumer.accept(selector);
				}
				selector.select();
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
