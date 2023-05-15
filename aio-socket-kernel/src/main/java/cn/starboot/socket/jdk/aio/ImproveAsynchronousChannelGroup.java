package cn.starboot.socket.jdk.aio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.concurrent.TimeUnit;

final class ImproveAsynchronousChannelGroup extends AsynchronousChannelGroup {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImproveAsynchronousChannelGroup.class);

	/**
	 * Initialize a new instance of this class.
	 *
	 * @param provider The asynchronous channel provider for this group
	 */
	protected ImproveAsynchronousChannelGroup(AsynchronousChannelProvider provider) {
		super(provider);
	}

	@Override
	public boolean isShutdown() {
		return false;
	}

	@Override
	public boolean isTerminated() {
		return false;
	}

	@Override
	public void shutdown() {

	}

	@Override
	public void shutdownNow() throws IOException {

	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return false;
	}
}
