package cn.starboot.socket.core.utils.concurrent;

import cn.starboot.socket.core.utils.concurrent.handle.ConcurrentWithReadHandler;
import cn.starboot.socket.core.utils.concurrent.handle.ConcurrentWithWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 自研抽象并发数据结构
 * 多读者-多作家 模型
 *
 * @param <T> T 对象类型
 *
 * @author MDong
 */
public abstract class AbstractConcurrent<T> implements Serializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConcurrent.class);

	private static final long serialVersionUID = 1058316930823743747L;

	private final AtomicInteger count = new AtomicInteger(0);

	private final Semaphore readWriteSemaphore = new Semaphore(1);

	private final Semaphore waitSemaphore = new Semaphore(1);

	private final T object;

	protected AbstractConcurrent(T object) {
		this.object = object;
	}

	T getObject() {
		return object;
	}

	public void handle(ConcurrentWithReadHandler<T> concurrentWithReadHandler) {
		// 判断是否第一个人，第一个人需要拿到读写互斥信号
		if (count.get() == 0) {
			if (waitSemaphore.tryAcquire()) {
				try {
					readWriteSemaphore.acquire();
				} catch (InterruptedException e) {
					LOGGER.error(e.getMessage());
				}
				// 1. 自增，让后来者直接进入读处理
				count.incrementAndGet();
				// 2. 通知所有等待读的线程
				this.notifyAll();
				// 3. 自己再去读。这三部曲速度最快，CPU利用最高。
				waitSemaphore.release();
			} else {
				synchronized (this) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				count.incrementAndGet();
			}
		} else {
			count.incrementAndGet();
		}

		try {
			concurrentWithReadHandler.handler(object);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}

		// 读完成
		if (count.decrementAndGet() == 0) {
			readWriteSemaphore.release();
		}
	}

	public void handle(ConcurrentWithWriteHandler<T> concurrentWithWriteHandler) {

		// 写操作简单，仅需进行互斥处理
		try {
			readWriteSemaphore.acquire();
			concurrentWithWriteHandler.handler(object);
		} catch (Exception e) {
			e.printStackTrace();
		}

		readWriteSemaphore.release();
	}

	public int size() {
		final int[] size = {0};
		final Consumer<Integer> callBackFunction = integer -> size[0] = integer;
		size(callBackFunction);
		return size[0];
	}

	public abstract void size(Consumer<Integer> callBackFunction);

	public abstract void clear();

}
