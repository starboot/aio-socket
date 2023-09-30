package cn.starboot.socket.jdk.aio.impl;

import cn.starboot.socket.jdk.aio.ImproveAsynchronousChannelGroup;
import cn.starboot.socket.jdk.aio.ImproveAsynchronousServerSocketChannel;
import cn.starboot.socket.jdk.aio.ImproveAsynchronousSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.channels.*;
import java.util.Set;
import java.util.concurrent.Future;

final class ImproveAsynchronousServerSocketChannelImpl extends ImproveAsynchronousServerSocketChannel {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImproveAsynchronousServerSocketChannelImpl.class);

	private final ServerSocketChannel serverSocketChannel;
	private final ImproveAsynchronousChannelGroup improveAsynchronousChannelGroup;
	private final ImproveAsynchronousChannelGroupImpl.Worker acceptWorker;
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
		acceptWorker = ((ImproveAsynchronousChannelGroupImpl) improveAsynchronousChannelGroup).getCommonWorker();
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
		if (acceptPending) {
			throw new AcceptPendingException();
		}
		acceptPending = true;
		this.acceptCompletionHandler = (CompletionHandler<ImproveAsynchronousSocketChannel, Object>) handler;
		this.attachment = attachment;
		doAccept();
	}

	public void doAccept() {
		try {
			SocketChannel socketChannel = null;
			if (acceptInvoker++ < ImproveAsynchronousChannelGroupImpl.MAX_INVOKER) {
				socketChannel = serverSocketChannel.accept();
			}
			if (socketChannel != null) {
				System.out.println("连接成功");
				ImproveAsynchronousSocketChannel asynchronousSocketChannel = new ImproveAsynchronousSocketChannelImpl(improveAsynchronousChannelGroup, socketChannel, true);
				//这行代码不要乱动
				socketChannel.configureBlocking(false);
				socketChannel.finishConnect();
				CompletionHandler<ImproveAsynchronousSocketChannel, Object> completionHandler = acceptCompletionHandler;
				Object attach = attachment;
				resetAccept();
				completionHandler.completed(asynchronousSocketChannel, attach);
				if (!acceptPending && selectionKey != null) {
					ImproveAsynchronousChannelGroupImpl.removeOps(selectionKey, SelectionKey.OP_ACCEPT);
				}
			}
			//首次注册selector
			else if (selectionKey == null) {
				acceptWorker.addRegister(selector -> {
					try {
						selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, ImproveAsynchronousServerSocketChannelImpl.this);
//                        selectionKey.attach(EnhanceAsynchronousServerSocketChannel.this);
					} catch (ClosedChannelException e) {
						acceptCompletionHandler.failed(e, attachment);
					}
				});
			} else {
				ImproveAsynchronousChannelGroupImpl.interestOps(acceptWorker, selectionKey, SelectionKey.OP_ACCEPT);
			}
		} catch (IOException e) {
			this.acceptCompletionHandler.failed(e, attachment);
		} finally {
			acceptInvoker = 0;
		}
	}

	private void resetAccept() {
		acceptPending = false;
		acceptCompletionHandler = null;
		attachment = null;
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
