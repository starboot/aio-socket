package cn.starboot.socket.core.udp;

import java.util.concurrent.ConcurrentLinkedQueue;

final class UDPWriteWorker implements Runnable {

	private final ConcurrentLinkedQueue<?> w = new ConcurrentLinkedQueue<>();

	private boolean isRunning = true;

	void shutdownWriteWorker() {
		isRunning = false;
	}

	@Override
	public void run() {

		while (isRunning) {
			// todo
		}
	}
}
