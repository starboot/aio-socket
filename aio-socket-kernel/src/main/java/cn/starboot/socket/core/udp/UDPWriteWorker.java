package cn.starboot.socket.core.udp;

import cn.starboot.socket.core.utils.pool.memory.MemoryUnit;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

final class UDPWriteWorker implements Runnable {

	private final ConcurrentLinkedQueue<MemoryUnit> writeQueue = new ConcurrentLinkedQueue<>();

	private boolean isRunning = true;

	private Semaphore semaphore = new Semaphore(1);

	void shutdownWriteWorker() {
		isRunning = false;
	}

	void addMemoryUnitToWriteQueue(MemoryUnit WriteMemoryUnit) {
		writeQueue.offer(WriteMemoryUnit);
	}

	@Override
	public void run() {

		while (isRunning) {
			if (writeQueue.poll() != null) {
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
