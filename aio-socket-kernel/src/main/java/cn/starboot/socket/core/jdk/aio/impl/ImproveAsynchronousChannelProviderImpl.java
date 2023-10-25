package cn.starboot.socket.core.jdk.aio.impl;

import cn.starboot.socket.core.jdk.aio.ImproveAsynchronousChannelGroup;
import cn.starboot.socket.core.jdk.aio.ImproveAsynchronousChannelProvider;
import cn.starboot.socket.core.jdk.aio.ImproveAsynchronousServerSocketChannel;
import cn.starboot.socket.core.jdk.aio.ImproveAsynchronousSocketChannel;
import cn.starboot.socket.core.utils.ThreadUtils;
import cn.starboot.socket.core.utils.pool.thread.AioCallerRunsPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.IllegalChannelGroupException;
import java.util.concurrent.*;

/**
 * @author MDong
 */
final class ImproveAsynchronousChannelProviderImpl extends ImproveAsynchronousChannelProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImproveAsynchronousChannelProviderImpl.class);

	private static volatile ImproveAsynchronousChannelGroup defaultImproveAsynchronousChannelGroup;

	private ImproveAsynchronousChannelGroup defaultAsynchronousChannelGroup() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Load default improve asynchronous channel group.");
		}
		if (defaultImproveAsynchronousChannelGroup == null) {
			synchronized (ImproveAsynchronousChannelProviderImpl.class) {
				if (defaultImproveAsynchronousChannelGroup == null) {
					defaultImproveAsynchronousChannelGroup =
							new ImproveAsynchronousChannelGroupImpl(this,
									ThreadUtils.getGroupExecutor(),
									ThreadUtils.MAX_POOL_SIZE_FOR_BOSS);
				}
			}
		}
		return defaultImproveAsynchronousChannelGroup;
	}

	@Override
	public ImproveAsynchronousChannelGroup openImproveAsynchronousChannelGroup(int nThreads,
																			   ThreadFactory threadFactory) {
		ThreadPoolExecutor groupExecutor =
				new ThreadPoolExecutor(nThreads,
						nThreads,
						0L,
						TimeUnit.SECONDS,
						new LinkedBlockingQueue<>(),
						threadFactory,
						new AioCallerRunsPolicy());
		groupExecutor.prestartCoreThread();
		return new ImproveAsynchronousChannelGroupImpl(this, groupExecutor, nThreads);
	}

	@Override
	public ImproveAsynchronousChannelGroup openImproveAsynchronousChannelGroup(ExecutorService executor,
																			   int initialSize) {
		return new ImproveAsynchronousChannelGroupImpl(this, executor, initialSize);
	}

	private ImproveAsynchronousChannelGroup toPort(ImproveAsynchronousChannelGroup group) {
		if (group == null) {
			return defaultAsynchronousChannelGroup();
		} else {
			if (!(group instanceof ImproveAsynchronousChannelGroupImpl))
				throw new IllegalChannelGroupException();
			return group;
		}
	}

	@Override
	public ImproveAsynchronousServerSocketChannel openImproveAsynchronousServerSocketChannel
			(ImproveAsynchronousChannelGroup group)
			throws IOException {
		return new ImproveAsynchronousServerSocketChannelImpl(toPort(group));
	}

	@Override
	public ImproveAsynchronousSocketChannel openImproveAsynchronousSocketChannel
			(ImproveAsynchronousChannelGroup group)
			throws IOException {
		return new ImproveAsynchronousSocketChannelImpl(toPort(group));
	}
}
