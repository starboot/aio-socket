package cn.starboot.socket.core.jdk.aio.impl;

import cn.starboot.socket.core.jdk.aio.ImproveAsynchronousChannelGroup;
import cn.starboot.socket.core.jdk.aio.ImproveAsynchronousServerSocketChannel;
import cn.starboot.socket.core.jdk.aio.ImproveAsynchronousSocketChannel;
import cn.starboot.socket.core.jdk.nio.NioEventLoopWorker;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.channels.*;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.function.Function;

final class ImproveAsynchronousServerSocketChannelImpl extends ImproveAsynchronousServerSocketChannel {

	private final ServerSocketChannel serverSocketChannel;

	private final ImproveAsynchronousChannelGroup improveAsynchronousChannelGroup;

	private final NioEventLoopWorker acceptWorker;

	private CompletionHandler<ImproveAsynchronousSocketChannel, Object> acceptCompletionHandler;

	private Object attachment;

	private SelectionKey selectionKey;

	private boolean acceptPending;

	private int acceptInvoker;

	private final Function<SocketChannel, ImproveAsynchronousSocketChannel> initAsynchronousSocketChannel =
			new Function<SocketChannel, ImproveAsynchronousSocketChannel>() {
				@Override
				public ImproveAsynchronousSocketChannel apply(SocketChannel socketChannel) {
					return new ImproveAsynchronousSocketChannelImpl(improveAsynchronousChannelGroup,
							initSocketChannel(socketChannel),
							true);
				}
			};

	/**
	 * Initializes a new instance of this class.
	 *
	 * @param group The provider that created this channel
	 */
	ImproveAsynchronousServerSocketChannelImpl(ImproveAsynchronousChannelGroup group) throws IOException {
		super(group.provider());
		this.improveAsynchronousChannelGroup = group;
		this.serverSocketChannel = ServerSocketChannel.open();
		this.serverSocketChannel.configureBlocking(false);
		acceptWorker = ((ImproveAsynchronousChannelGroupImpl) improveAsynchronousChannelGroup).getMainReactor();
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

	@SuppressWarnings("unchecked")
	@Override
	public <A> void accept(A attachment, CompletionHandler<ImproveAsynchronousSocketChannel, ? super A> handler) {
		if (acceptPending) {
			throw new AcceptPendingException();
		}
		acceptPending = true;
		this.acceptCompletionHandler = (CompletionHandler<ImproveAsynchronousSocketChannel, Object>) handler;
		this.attachment = attachment;
		doAccept();
	}

	private boolean isDirectAccept() {
		return acceptInvoker++ < ImproveInherentUtil.MAX_INVOKER;
	}

	private SocketChannel initSocketChannel(SocketChannel socketChannel) {
		try {
			socketChannel.configureBlocking(false);
			socketChannel.finishConnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return socketChannel;
	}

	private void finishAccept(SocketChannel socketChannel) {
		ImproveAsynchronousSocketChannel asynchronousSocketChannel =
				initAsynchronousSocketChannel.apply(socketChannel);
		acceptPending = false;
		acceptCompletionHandler.completed(asynchronousSocketChannel, attachment);
	}

	private void initRegister() {
		acceptWorker.addRegister(
				selector -> {
					try {
						selectionKey = serverSocketChannel.register(selector,
								SelectionKey.OP_ACCEPT,
								ImproveAsynchronousServerSocketChannelImpl.this);
					} catch (ClosedChannelException closedChannelException) {
						acceptCompletionHandler.failed(closedChannelException, attachment);
					}
				});
	}

	public void doAccept() {
		try {
			SocketChannel socketChannel = isDirectAccept() ? serverSocketChannel.accept() : null;
			if (socketChannel != null) {
				finishAccept(socketChannel);
				if (!acceptPending && selectionKey != null) {
					ImproveInherentUtil.removeOps(selectionKey, SelectionKey.OP_ACCEPT);
				}
			} else if (selectionKey == null) {
				initRegister();
			} else {
				ImproveInherentUtil.interestOps(acceptWorker, selectionKey, SelectionKey.OP_ACCEPT);
			}
		} catch (IOException e) {
			this.acceptCompletionHandler.failed(e, attachment);
		} finally {
			acceptInvoker = 0;
		}
	}

	@Override
	public Future<ImproveAsynchronousSocketChannel> accept() {
		throw new UnsupportedOperationException("Unsupported Operation Exception: " +
				"Future<ImproveAsynchronousSocketChannel> accept()");
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
