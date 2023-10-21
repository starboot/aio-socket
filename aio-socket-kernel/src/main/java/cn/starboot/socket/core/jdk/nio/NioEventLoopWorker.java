package cn.starboot.socket.core.jdk.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * nio轮训工作者
 *
 * @author MDong
 */
public class NioEventLoopWorker implements Runnable {

	/**
	 * 当前NioEventLoopWorker绑定的Selector
	 */
	private final Selector nioEventLoopSelector;

	private final Set<SelectionKey> selectionKeys;

	/**
	 * 当前worker所属线程
	 */
	private Thread nioEventLoopThread;

	/**
	 * NioEventLoopWorker运行状态
	 */
	private boolean isRunning = true;

	/**
	 * 用于处理轮训结果的声明式函数
	 */
	private final Consumer<SelectionKey> nioEventLoopSelectionKey;

	/**
	 * 待注册的事件
	 */
	private final Queue<Consumer<Selector>> registerWaitQueue = new ConcurrentLinkedQueue<>();

	public NioEventLoopWorker(Selector selector, Consumer<SelectionKey> consumer) {
		this.nioEventLoopSelector = selector;
		this.nioEventLoopSelectionKey = consumer;
		this.selectionKeys = nioEventLoopSelector.selectedKeys();
	}

	public void shutdown() {
		this.isRunning = false;
	}

	public Thread getNioEventLoopThread() {
		return nioEventLoopThread;
	}

	public Selector getSelector() {
		return nioEventLoopSelector;
	}

	/**
	 * 注册事件
	 */
	public void addRegister(Consumer<Selector> register) {
		registerWaitQueue.offer(register);
		nioEventLoopSelector.wakeup();
	}

	@Override
	public final void run() {
		nioEventLoopThread = Thread.currentThread();
		try {
			while (isRunning) {
				while (!registerWaitQueue.isEmpty()) {
					registerWaitQueue.poll().accept(nioEventLoopSelector);
				}
				Iterator<SelectionKey> iterator = selectionKeys.iterator();
				while (iterator.hasNext()) {
					nioEventLoopSelectionKey.accept(iterator.next());
					iterator.remove();
				}
				nioEventLoopSelector.select();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 关闭selector
		try {
			nioEventLoopSelector.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
