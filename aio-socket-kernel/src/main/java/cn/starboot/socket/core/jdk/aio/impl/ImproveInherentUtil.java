package cn.starboot.socket.core.jdk.aio.impl;

import cn.starboot.socket.core.jdk.nio.NioEventLoopWorker;

import java.nio.channels.SelectionKey;

final class ImproveInherentUtil {

	private ImproveInherentUtil() {
	}

	/**
	 * 递归回调次数上限
	 */
	static final int MAX_INVOKER = 8;


	/**
	 * 移除关注事件
	 *
	 * @param selectionKey 待操作的selectionKey
	 * @param opt          移除的事件
	 */
	static void removeOps(SelectionKey selectionKey, int opt) {
		if ((selectionKey.interestOps() & opt) != 0) {
			selectionKey.interestOps(selectionKey.interestOps() & ~opt);
		}
	}

	static void interestOps(NioEventLoopWorker worker, SelectionKey selectionKey, int opt) {
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
