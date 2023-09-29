package cn.starboot.socket.jdk.aio.impl;

import cn.starboot.socket.jdk.aio.ImproveAsynchronousChannelGroup;
import cn.starboot.socket.jdk.aio.ImproveAsynchronousChannelProvider;
import cn.starboot.socket.jdk.aio.ImproveAsynchronousServerSocketChannel;
import cn.starboot.socket.jdk.aio.ImproveAsynchronousSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.IllegalChannelGroupException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

public final class ImproveAsynchronousChannelProviderImpl extends ImproveAsynchronousChannelProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImproveAsynchronousChannelProviderImpl.class);

	private static volatile ImproveAsynchronousChannelGroup defaultImproveAsynchronousChannelGroup;

	private ImproveAsynchronousChannelGroup defaultAsynchronousChannelGroup() {
		if (defaultImproveAsynchronousChannelGroup == null) {
			synchronized (ImproveAsynchronousChannelProviderImpl.class) {
				if (defaultImproveAsynchronousChannelGroup == null) {
					defaultImproveAsynchronousChannelGroup = new ImproveAsynchronousChannelGroupImpl(null);
				}
			}
		}
		return defaultImproveAsynchronousChannelGroup;
	}

	@Override
	public ImproveAsynchronousChannelGroup openImproveAsynchronousChannelGroup(int nThreads, ThreadFactory threadFactory) throws IOException {
		return null;
	}

	@Override
	public ImproveAsynchronousChannelGroup openImproveAsynchronousChannelGroup(ExecutorService executor, int initialSize) throws IOException {
		return null;
	}

	private ImproveAsynchronousChannelGroup toPort(ImproveAsynchronousChannelGroup group) throws IOException {
		if (group == null) {
			return defaultAsynchronousChannelGroup();
		} else {
			if (!(group instanceof ImproveAsynchronousChannelGroupImpl))
				throw new IllegalChannelGroupException();
			return group;
		}
	}

	@Override
	public ImproveAsynchronousServerSocketChannel openImproveAsynchronousServerSocketChannel(ImproveAsynchronousChannelGroup group) throws IOException {
		return new ImproveAsynchronousServerSocketChannelImpl(toPort(group));
	}

	@Override
	public ImproveAsynchronousSocketChannel openImproveAsynchronousSocketChannel(ImproveAsynchronousChannelGroup group) throws IOException {
		throw new UnsupportedEncodingException("unsupported");
	}
}
