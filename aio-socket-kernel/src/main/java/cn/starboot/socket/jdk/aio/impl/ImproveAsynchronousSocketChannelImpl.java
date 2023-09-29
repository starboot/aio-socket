package cn.starboot.socket.jdk.aio.impl;

import cn.starboot.socket.jdk.aio.ImproveAsynchronousChannelProvider;
import cn.starboot.socket.jdk.aio.ImproveAsynchronousSocketChannel;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ImproveAsynchronousSocketChannelImpl extends ImproveAsynchronousSocketChannel {

	/**
	 * Initializes a new instance of this class.
	 *
	 * @param provider The provider that created this channel
	 */
	protected ImproveAsynchronousSocketChannelImpl(ImproveAsynchronousChannelProvider provider) {
		super(provider);
	}

	@Override
	public ImproveAsynchronousSocketChannel bind(SocketAddress local) throws IOException {
		return null;
	}

	@Override
	public <T> ImproveAsynchronousSocketChannel setOption(SocketOption<T> name, T value) throws IOException {
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
	public ImproveAsynchronousSocketChannel shutdownInput() throws IOException {
		return null;
	}

	@Override
	public ImproveAsynchronousSocketChannel shutdownOutput() throws IOException {
		return null;
	}

	@Override
	public SocketAddress getRemoteAddress() throws IOException {
		return null;
	}

	@Override
	public <A> void connect(SocketAddress remote, A attachment, CompletionHandler<Void, ? super A> handler) {

	}

	@Override
	public Future<Void> connect(SocketAddress remote) {
		return null;
	}

	@Override
	public <A> void read(ByteBuffer dst, long timeout, TimeUnit unit, A attachment, CompletionHandler<Integer, ? super A> handler) {

	}

	@Override
	public Future<Integer> read(ByteBuffer dst) {
		return null;
	}

	@Override
	public <A> void read(ByteBuffer[] dsts, int offset, int length, long timeout, TimeUnit unit, A attachment, CompletionHandler<Long, ? super A> handler) {

	}

	@Override
	public <A> void write(ByteBuffer src, long timeout, TimeUnit unit, A attachment, CompletionHandler<Integer, ? super A> handler) {

	}

	@Override
	public Future<Integer> write(ByteBuffer src) {
		return null;
	}

	@Override
	public <A> void write(ByteBuffer[] srcs, int offset, int length, long timeout, TimeUnit unit, A attachment, CompletionHandler<Long, ? super A> handler) {

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
