package cn.starboot.socket.jdk.aio.impl;

import cn.starboot.socket.jdk.aio.ImproveAsynchronousChannelGroup;
import cn.starboot.socket.jdk.aio.ImproveLinuxAsynchronousServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

public final class ImproveLinuxAsynchronousChannelProvider extends AsynchronousChannelProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImproveLinuxAsynchronousChannelProvider.class);

	@Override
	public AsynchronousChannelGroup openAsynchronousChannelGroup(int nThreads, ThreadFactory threadFactory) throws IOException {
		return new ImproveAsynchronousChannelGroup(this);
	}

	@Override
	public AsynchronousChannelGroup openAsynchronousChannelGroup(ExecutorService executor, int initialSize) throws IOException {
		return new ImproveAsynchronousChannelGroup(this);
	}

	@Override
	public AsynchronousServerSocketChannel openAsynchronousServerSocketChannel(AsynchronousChannelGroup group) throws IOException {
		return new ImproveLinuxAsynchronousServerSocketChannel(checkAndGet(group));
	}

	@Override
	public AsynchronousSocketChannel openAsynchronousSocketChannel(AsynchronousChannelGroup group) throws IOException {
		throw new UnsupportedEncodingException("unsupported");
	}

	private ImproveAsynchronousChannelGroup checkAndGet(AsynchronousChannelGroup group) {
		if (group == null) {
			return defaultAsynchronousChannelGroup();
		}
		if (!(group instanceof ImproveAsynchronousChannelGroup)) {
			throw new RuntimeException("invalid class");
		}
		return (ImproveAsynchronousChannelGroup) group;
	}

	private static volatile ImproveAsynchronousChannelGroup defaultAsynchronousChannelGroup;

	private ImproveAsynchronousChannelGroup defaultAsynchronousChannelGroup() {
		if (defaultAsynchronousChannelGroup == null) {
			synchronized (ImproveLinuxAsynchronousChannelProvider.class) {
				if (defaultAsynchronousChannelGroup == null) {
					defaultAsynchronousChannelGroup = new ImproveAsynchronousChannelGroup(null);
				}
			}
		}
		return defaultAsynchronousChannelGroup;
	}
}
