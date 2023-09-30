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
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.Set;
import java.util.concurrent.Future;

final class ImproveAsynchronousServerSocketChannelImpl extends ImproveAsynchronousServerSocketChannel {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImproveAsynchronousServerSocketChannelImpl.class);

	private final ServerSocketChannel serverSocketChannel;
	private final ImproveAsynchronousChannelGroup improveAsynchronousChannelGroup;
	private CompletionHandler<ImproveAsynchronousSocketChannel, Object> acceptCompletionHandler;
	private Object attachment;
	private SelectionKey selectionKey;
	private boolean acceptPending;

	private int acceptInvoker;

	/**
	 * Initializes a new instance of this class.
	 *
	 * @param group The provider that created this channel
	 */
	protected ImproveAsynchronousServerSocketChannelImpl(ImproveAsynchronousChannelGroup group) throws IOException {
		super(group.provider());
		this.improveAsynchronousChannelGroup = group;
		this.serverSocketChannel = ServerSocketChannel.open();
		this.serverSocketChannel.configureBlocking(false);
	}


	@Override
	public ImproveAsynchronousServerSocketChannel bind(SocketAddress local, int backlog) throws IOException {
		this.serverSocketChannel.bind(local, backlog);
		return this;
	}

	@Override
	public <T> ImproveAsynchronousServerSocketChannel setOption(SocketOption<T> name, T value) throws IOException {
		this.serverSocketChannel.setOption(name, value);
		return this;
	}

	@Override
	public <T> T getOption(SocketOption<T> name) throws IOException {
		return this.serverSocketChannel.getOption(name);
	}

	@Override
	public Set<SocketOption<?>> supportedOptions() {
		return this.serverSocketChannel.supportedOptions();
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
		return this.serverSocketChannel.getLocalAddress();
	}

	@Override
	public boolean isOpen() {
		return this.serverSocketChannel.isOpen();
	}

	@Override
	public void close() throws IOException {
		this.serverSocketChannel.close();
	}
}
