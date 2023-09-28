package cn.starboot.socket.jdk.aio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Set;
import java.util.concurrent.Future;

public final class ImproveLinuxAsynchronousServerSocketChannel extends AsynchronousServerSocketChannel {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImproveLinuxAsynchronousServerSocketChannel.class);

	/**
	 * Initializes a new instance of this class.
	 *
	 * @param improveAsynchronousChannelGroup The provider that created this channel
	 */
	public ImproveLinuxAsynchronousServerSocketChannel(ImproveAsynchronousChannelGroup improveAsynchronousChannelGroup) {
		super(improveAsynchronousChannelGroup.provider());
	}

	@Override
	public AsynchronousServerSocketChannel bind(SocketAddress local, int backlog) throws IOException {
		return null;
	}

	@Override
	public <T> AsynchronousServerSocketChannel setOption(SocketOption<T> name, T value) throws IOException {
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
	public <A> void accept(A attachment, CompletionHandler<AsynchronousSocketChannel, ? super A> handler) {

	}

	@Override
	public Future<AsynchronousSocketChannel> accept() {
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
