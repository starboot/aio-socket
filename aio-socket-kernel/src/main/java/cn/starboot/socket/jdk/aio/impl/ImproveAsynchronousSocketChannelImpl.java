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
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

final class ImproveAsynchronousSocketChannelImpl extends ImproveAsynchronousSocketChannel {

	private final boolean isServerCreate;

	protected final SocketChannel socketChannel;

	/**
	 * 用于接收 read 通道数据的缓冲区，经解码后腾出缓冲区以供下一批数据的读取
	 */
	private ByteBuffer readBuffer;
	/**
	 * 存放待输出数据的缓冲区
	 */
	private ByteBuffer writeBuffer;

	/**
	 * read 回调事件处理器
	 */
	private CompletionHandler<Number, Object> readCompletionHandler;
	/**
	 * write 回调事件处理器
	 */
	private CompletionHandler<Number, Object> writeCompletionHandler;
	/**
	 * read 回调事件关联绑定的附件对象
	 */
	private Object readAttachment;
	/**
	 * write 回调事件关联绑定的附件对象
	 */
	private Object writeAttachment;
	private SelectionKey readSelectionKey;

	/**
	 * Initializes a new instance of this class.
	 *
	 * @param group The provider that created this channel
	 */
	protected ImproveAsynchronousSocketChannelImpl(ImproveAsynchronousChannelGroup group, SocketChannel socketChannel)
			throws IOException {
		this(group, socketChannel, false);
	}

	protected ImproveAsynchronousSocketChannelImpl(ImproveAsynchronousChannelGroup group,
												   SocketChannel socketChannel,
												   boolean isServerCreate)
			throws IOException {
		super(group.provider());
		this.isServerCreate = isServerCreate;

		this.socketChannel = socketChannel;
	}

	@Override
	public ImproveAsynchronousSocketChannel bind(SocketAddress local)
			throws IOException {
		this.socketChannel.bind(local);
		return this;
	}

	@Override
	public <T> ImproveAsynchronousSocketChannel setOption(SocketOption<T> name, T value)
			throws IOException {
		this.socketChannel.setOption(name, value);
		return this;
	}

	@Override
	public <T> T getOption(SocketOption<T> name) throws IOException {
		return this.socketChannel.getOption(name);
	}

	@Override
	public Set<SocketOption<?>> supportedOptions() {
		return this.socketChannel.supportedOptions();
	}

	@Override
	public ImproveAsynchronousSocketChannel shutdownInput() throws IOException {
		this.socketChannel.shutdownInput();
		return this;
	}

	@Override
	public ImproveAsynchronousSocketChannel shutdownOutput() throws IOException {
		this.socketChannel.shutdownOutput();
		return this;
	}

	@Override
	public SocketAddress getRemoteAddress() throws IOException {
		return this.socketChannel.getRemoteAddress();
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
	public <A> void read(Supplier<MemoryUnit> supplier,
							   long timeout,
							   TimeUnit unit,
							   A attachment,
							   CompletionHandler<Integer, ? super A> handler)
	{
		supplier.get();
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
		return this.socketChannel.getLocalAddress();
	}

	@Override
	public boolean isOpen() {
		return this.socketChannel.isOpen();
	}

	@Override
	public void close() throws IOException {
		IOException exception = null;
		try {
			if (this.socketChannel.isOpen()) {
				this.socketChannel.close();
			}
		} catch (IOException e) {
			exception = e;
		}
		if (this.readSelectionKey != null) {
			this.readSelectionKey.cancel();
			this.readSelectionKey = null;
		}
//		SelectionKey key = this.socketChannel.keyFor(commonWorker.selector);
//		if (key != null) {
//			key.cancel();
//		}
		if (exception != null) {
			throw exception;
		}
	}
}
