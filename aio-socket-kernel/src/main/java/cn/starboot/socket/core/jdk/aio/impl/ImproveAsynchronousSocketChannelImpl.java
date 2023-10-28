package cn.starboot.socket.core.jdk.aio.impl;

import cn.starboot.socket.core.jdk.aio.ImproveAsynchronousChannelGroup;
import cn.starboot.socket.core.jdk.aio.ImproveAsynchronousSocketChannel;
import cn.starboot.socket.core.jdk.nio.NioEventLoopWorker;
import cn.starboot.socket.core.utils.pool.memory.MemoryUnit;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

final class ImproveAsynchronousSocketChannelImpl extends ImproveAsynchronousSocketChannel {

	private final boolean isServerCreate;

	protected final SocketChannel socketChannel;

	/**
	 * 处理 read 事件的线程资源
	 */
	private final NioEventLoopWorker readWorker;
	/**
	 * 处理 write 事件的线程资源
	 */
	protected final NioEventLoopWorker commonWorker;

	/**
	 * 用于接收 read 通道数据的缓冲区，经解码后腾出缓冲区以供下一批数据的读取
	 */
	private MemoryUnit readMemoryUnit;
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
	 * 中断写操作
	 */
	private boolean writeInterrupted;

	private final ImproveAsynchronousChannelGroup group;

	/**
	 * Initializes a new instance of this class.
	 *
	 * @param group The provider that created this channel
	 */
	protected ImproveAsynchronousSocketChannelImpl(ImproveAsynchronousChannelGroup group)
			throws IOException {
		this(group, SocketChannel.open(), false);
	}

	protected ImproveAsynchronousSocketChannelImpl(ImproveAsynchronousChannelGroup group,
												   SocketChannel socketChannel,
												   boolean isServerCreate) {
		super(group.provider());
		this.isServerCreate = isServerCreate;
		this.socketChannel = socketChannel;
		this.group = group;
		this.readWorker = ((ImproveAsynchronousChannelGroupImpl) group).getSubReactor();
		this.commonWorker = ((ImproveAsynchronousChannelGroupImpl) group).getMainReactor();
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

	<A> void implConnect(SocketAddress remote,
						 A attachment,
						 CompletionHandler<Void, ? super A> handler) {
		if (isServerCreate) {
			try {
				throw new UnsupportedEncodingException("unsupported");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		// 实现连接
		if (this.group.isTerminated()) {
			throw new ShutdownChannelGroupException();
		}
		if (socketChannel.isConnected()) {
			throw new AlreadyConnectedException();
		}
		if (socketChannel.isConnectionPending()) {
			throw new ConnectionPendingException();
		}
		doConnect(remote, attachment, handler);
	}

	private boolean doConnect0(SocketAddress remote) throws IOException {
		boolean connected = socketChannel.isConnectionPending();
		if (connected || socketChannel.connect(remote)) {
			connected = socketChannel.finishConnect();
		}
		//这行代码不要乱动
		socketChannel.configureBlocking(false);
		return connected;
	}

	private <A> void initConnectRegister(Runnable runnable,
										 A attachment,
										 CompletionHandler<Void, ? super A> completionHandler) {
		commonWorker.addRegister(
				selector -> {
					try {
						socketChannel.register(selector, SelectionKey.OP_CONNECT, runnable);
					} catch (ClosedChannelException e) {
						completionHandler.failed(e, attachment);
					}
				});
	}

	public <A> void doConnect(SocketAddress remote, A attachment, CompletionHandler<Void, ? super A> completionHandler) {
		try {
			if (doConnect0(remote)) {
				completionHandler.completed(null, attachment);
			} else {
				initConnectRegister(() -> doConnect(remote, attachment, completionHandler),
						attachment,
						completionHandler);
			}
		} catch (IOException e) {
			completionHandler.failed(e, attachment);
		}
	}

	@Override
	public <A> void connect(SocketAddress remote,
							A attachment,
							CompletionHandler<Void, ? super A> handler) {
		if (handler == null)
			throw new NullPointerException("'handler' is null");
		implConnect(remote, attachment, handler);
	}

	@Override
	public Future<Void> connect(SocketAddress remote) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <A> void read(Function<Boolean, MemoryUnit> function,
						 long timeout,
						 TimeUnit unit,
						 A attachment,
						 CompletionHandler<Integer, ? super A> handler) {
		if (timeout > 0) {
			throw new UnsupportedOperationException();
		}
		read0(function, attachment, handler);
	}

	private Function<Boolean, MemoryUnit> memoryUnitFunction;

	private <V extends Number, A> void read0(Function<Boolean, MemoryUnit> function, A attachment, CompletionHandler<V, ? super A> handler) {
		if (this.readCompletionHandler != null) {
			throw new ReadPendingException();
		}
		if (this.memoryUnitFunction == null) {
			this.memoryUnitFunction = function;
		}
		this.readAttachment = attachment;
		this.readCompletionHandler = (CompletionHandler<Number, Object>) handler;
		doRead();
	}

	@Override
	public Future<Integer> read(ByteBuffer dst) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <A> void read(ByteBuffer[] dsts,
						 int offset,
						 int length,
						 long timeout,
						 TimeUnit unit,
						 A attachment,
						 CompletionHandler<Long, ? super A> handler) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <A> void write(MemoryUnit src,
						  long timeout,
						  TimeUnit unit,
						  A attachment,
						  CompletionHandler<Integer, ? super A> handler) {
		if (timeout > 0) {
			throw new UnsupportedOperationException();
		}
		write0(src.buffer(), attachment, handler);
	}

	private <V extends Number, A> void write0(ByteBuffer writeBuffer, A attachment, CompletionHandler<V, ? super A> handler) {
		if (this.writeCompletionHandler != null) {
			throw new WritePendingException();
		}
		this.writeBuffer = writeBuffer;
		this.writeAttachment = attachment;
		this.writeCompletionHandler = (CompletionHandler<Number, Object>) handler;
		while (doWrite()) ;
	}

	@Override
	public Future<Integer> write(ByteBuffer src) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <A> void write(ByteBuffer[] srcs,
						  int offset,
						  int length,
						  long timeout,
						  TimeUnit unit,
						  A attachment,
						  CompletionHandler<Long, ? super A> handler) {
		throw new UnsupportedOperationException();
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
		SelectionKey key = this.socketChannel.keyFor(commonWorker.getSelector());
		if (key != null) {
			key.cancel();
		}
		if (exception != null) {
			throw exception;
		}
	}

	private void readFinish(int readSize) {
		CompletionHandler<Number, Object> completionHandler = readCompletionHandler;
		Object attach = readAttachment;
		resetRead();
		completionHandler.completed(readSize, attach);
	}

	private void initReadRegister() {
		readWorker.addRegister(selector -> {
			try {
				readSelectionKey = socketChannel.register(selector, SelectionKey.OP_READ, ImproveAsynchronousSocketChannelImpl.this);
			} catch (ClosedChannelException e) {
				readCompletionHandler.failed(e, readAttachment);
			}
		});
	}

	private void readThrowableHandler(Throwable throwable) {
		if (readCompletionHandler == null) {
			throwable.printStackTrace();
			try {
				close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		} else {
			readCompletionHandler.failed(throwable, readAttachment);
		}
	}

	private byte readInvoker = ImproveInherentUtil.MAX_INVOKER;

	public final void doRead() {
		try {
			if (readCompletionHandler == null) {
				return;
			}
			boolean directRead = readInvoker++ < ImproveInherentUtil.MAX_INVOKER;

			int readSize = 0;
			boolean hasRemain = true;
			if (directRead) {
				// 在这里申请内存
				this.readMemoryUnit = memoryUnitFunction.apply(true);
				readSize = socketChannel.read(readMemoryUnit.buffer());
				hasRemain = readMemoryUnit.buffer().hasRemaining();
			}
			if (readSize != 0 || !hasRemain) {
				readFinish(readSize);
				if (readCompletionHandler == null && readSelectionKey != null) {
					// 当前用户已关闭，移除其select事件
					ImproveInherentUtil.removeOps(readSelectionKey, SelectionKey.OP_READ);
				}
			} else if (readSelectionKey == null) {
				initReadRegister();
			} else {
				// 判断是否申请了内存
				if (directRead) {
					// 在这里应该释放内存
					memoryUnitFunction.apply(false);
				}
				ImproveInherentUtil.interestOps(readWorker, readSelectionKey, SelectionKey.OP_READ);
			}
		} catch (Throwable e) {
			readThrowableHandler(e);
		} finally {
			readInvoker = 0;
		}
	}

	private void resetRead() {
		readCompletionHandler = null;
		readAttachment = null;
		readMemoryUnit = null;
//		if (!readMemoryUnit.buffer().hasRemaining()) {
//			System.out.println("这里有bug");
//			readMemoryUnit.clean();
//			readMemoryUnit = null;
//		}
	}

	private void writeFinish(int writeSize) {
		CompletionHandler<Number, Object> completionHandler = writeCompletionHandler;
		Object attach = writeAttachment;
		resetWrite();
		writeInterrupted = true;
		completionHandler.completed(writeSize, attach);
	}

	private void initWriteRegister() {
		commonWorker.addRegister(selector -> {
			try {
				socketChannel.register(selector, SelectionKey.OP_WRITE, ImproveAsynchronousSocketChannelImpl.this);
			} catch (ClosedChannelException e) {
				writeCompletionHandler.failed(e, writeAttachment);
			}
		});
	}

	private void writeThrowableHandler(Throwable throwable) {
		if (writeCompletionHandler == null) {
			throwable.printStackTrace();
			try {
				close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		} else {
			writeCompletionHandler.failed(throwable, writeAttachment);
		}
	}

	public final boolean doWrite() {
		if (writeInterrupted) {
			writeInterrupted = false;
			return false;
		}
		try {
			int writeSize = socketChannel.write(writeBuffer);
			if (writeSize != 0 || !writeBuffer.hasRemaining()) {
				writeFinish(writeSize);
				if (!writeInterrupted) {
					return true;
				}
				writeInterrupted = false;
			} else {
				SelectionKey commonSelectionKey = socketChannel.keyFor(commonWorker.getSelector());
				if (commonSelectionKey == null) {
					initWriteRegister();
				} else {
					ImproveInherentUtil.interestOps(commonWorker, commonSelectionKey, SelectionKey.OP_WRITE);
				}
			}
		} catch (Throwable e) {
			writeThrowableHandler(e);
		}
		return false;
	}

	private void resetWrite() {
		writeAttachment = null;
		writeCompletionHandler = null;
		writeBuffer = null;
	}
}
