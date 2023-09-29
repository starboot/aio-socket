package cn.starboot.socket.jdk.aio.impl;

import cn.starboot.socket.jdk.aio.ImproveAsynchronousChannelGroup;
import cn.starboot.socket.jdk.aio.ImproveAsynchronousServerSocketChannel;
import cn.starboot.socket.jdk.aio.ImproveAsynchronousSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.channels.CompletionHandler;
import java.util.Set;
import java.util.concurrent.Future;

public final class ImproveAsynchronousServerSocketChannelImpl extends ImproveAsynchronousServerSocketChannel {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImproveAsynchronousServerSocketChannelImpl.class);

	/**
	 * Initializes a new instance of this class.
	 *
	 * @param group The provider that created this channel
	 */
	protected ImproveAsynchronousServerSocketChannelImpl(ImproveAsynchronousChannelGroup group) {
		super(group.provider());
	}


	@Override
	public ImproveAsynchronousServerSocketChannel bind(SocketAddress local, int backlog) throws IOException {
		return null;
	}

	@Override
	public <T> ImproveAsynchronousServerSocketChannel setOption(SocketOption<T> name, T value) throws IOException {
		return null;
	}

	@Override
	public <T> T getOption(SocketOption<T> name) throws IOException {
		return null;
	}

	@Override
	public Set<SocketOption<?>> supportedOptions() {
		return null;
	}

	@Override
	public <A> void accept(A attachment, CompletionHandler<ImproveAsynchronousSocketChannel, ? super A> handler) {

	}

	@Override
	public Future<ImproveAsynchronousSocketChannel> accept() {
		return null;
	}

	@Override
	public SocketAddress getLocalAddress() throws IOException {
		return null;
	}

	@Override
	public boolean isOpen() {
		return false;
	}

	@Override
	public void close() throws IOException {

	}
}
