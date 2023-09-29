package cn.starboot.socket.jdk.aio.impl;

import cn.starboot.socket.jdk.aio.ImproveAsynchronousChannelGroup;
import cn.starboot.socket.jdk.aio.ImproveAsynchronousSocketChannel;
import cn.starboot.socket.utils.pool.memory.MemoryUnit;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

final class ImproveAsynchronousSocketChannelImpl extends ImproveAsynchronousSocketChannel {

	private final boolean isServerCreate;

	/**
	 * Initializes a new instance of this class.
	 *
	 * @param group The provider that created this channel
	 */
	protected ImproveAsynchronousSocketChannelImpl(ImproveAsynchronousChannelGroup group) {
		this(group, false);
	}

	protected ImproveAsynchronousSocketChannelImpl(ImproveAsynchronousChannelGroup group,
												   boolean isServerCreate) {
		super(group.provider());
		this.isServerCreate = isServerCreate;
	}

	@Override
	public ImproveAsynchronousSocketChannel bind(SocketAddress local)
			throws IOException {
		return null;
	}

	@Override
	public <T> ImproveAsynchronousSocketChannel setOption(SocketOption<T> name, T value)
			throws IOException {
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

	<A> Future<Void> implConnect(SocketAddress remote,
								 A attachment,
								 CompletionHandler<Void,? super A> handler) {
		if (isServerCreate) {
			try {
				throw new UnsupportedEncodingException("unsupported");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		// 实现连接
		return null;
	}

	@Override
	public <A> void connect(SocketAddress remote,
							A attachment,
							CompletionHandler<Void, ? super A> handler)
	{
		if (handler == null)
			throw new NullPointerException("'handler' is null");
		implConnect(remote, attachment, handler);
	}

	@Override
	public Future<Void> connect(SocketAddress remote) {
		return implConnect(remote, null, null);
	}

	@Override
	public <A> MemoryUnit read(Supplier<MemoryUnit> dst,
							   long timeout,
							   TimeUnit unit,
							   A attachment,
							   CompletionHandler<Integer, ? super A> handler)
	{
		return dst.get();
	}

	@Override
	public Future<Integer> read(ByteBuffer dst) {
		return null;
	}

	@Override
	public <A> void read(ByteBuffer[] dsts,
						 int offset,
						 int length,
						 long timeout,
						 TimeUnit unit,
						 A attachment,
						 CompletionHandler<Long, ? super A> handler)
	{

	}

	@Override
	public <A> void write(MemoryUnit src,
						  long timeout,
						  TimeUnit unit,
						  A attachment,
						  CompletionHandler<Integer, ? super A> handler)
	{

	}

	@Override
	public Future<Integer> write(ByteBuffer src) {
		return null;
	}

	@Override
	public <A> void write(ByteBuffer[] srcs,
						  int offset,
						  int length,
						  long timeout,
						  TimeUnit unit,
						  A attachment,
						  CompletionHandler<Long, ? super A> handler)
	{

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
