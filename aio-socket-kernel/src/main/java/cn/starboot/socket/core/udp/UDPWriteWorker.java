package cn.starboot.socket.core.udp;

import cn.starboot.socket.core.utils.concurrent.queue.ConcurrentWithQueue;
import cn.starboot.socket.core.utils.pool.memory.MemoryUnit;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

final class UDPWriteWorker implements Runnable {

	private final ConcurrentWithQueue<MemoryUnit> writeQueue = new ConcurrentWithQueue<>(new LinkedBlockingQueue<>());

	private boolean isRunning = true;

	private final Semaphore semaphore = new Semaphore(1);

	void shutdownWriteWorker() {
		isRunning = false;
	}

	void addMemoryUnitToWriteQueue(MemoryUnit WriteMemoryUnit) {
		writeQueue.offer(WriteMemoryUnit, new Consumer<Boolean>() {
			@Override
			public void accept(Boolean aBoolean) {
				if (aBoolean) {
					semaphore.release();
				}
			}
		});
	}

	@Override
	public void run() {

		while (isRunning) {
			if (writeQueue != null) {
				//
			} else {
				try {
					semaphore.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
