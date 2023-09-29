package cn.starboot.socket.jdk.aio;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.channels.*;
import java.util.concurrent.Future;

public abstract class ImproveAsynchronousServerSocketChannel
		implements AsynchronousChannel, NetworkChannel {
	private final ImproveAsynchronousChannelProvider provider;

	/**
	 * Initializes a new instance of this class.
	 *
	 * @param  provider
	 *         The provider that created this channel
	 */
	protected ImproveAsynchronousServerSocketChannel(ImproveAsynchronousChannelProvider provider) {
		this.provider = provider;
	}

	/**
	 * Returns the provider that created this channel.
	 *
	 * @return  The provider that created this channel
	 */
	public final ImproveAsynchronousChannelProvider provider() {
		return provider;
	}

	/**
	 * Opens an asynchronous server-socket channel.
	 *
	 * <p> The new channel is created by invoking the {@link
	 * java.nio.channels.spi.AsynchronousChannelProvider#openAsynchronousServerSocketChannel
	 * openAsynchronousServerSocketChannel} method on the {@link
	 * java.nio.channels.spi.AsynchronousChannelProvider} object that created
	 * the given group. If the group parameter is {@code null} then the
	 * resulting channel is created by the system-wide default provider, and
	 * bound to the <em>default group</em>.
	 *
	 * @param   group
	 *          The group to which the newly constructed channel should be bound,
	 *          or {@code null} for the default group
	 *
	 * @return  A new asynchronous server socket channel
	 *
	 * @throws ShutdownChannelGroupException
	 *          If the channel group is shutdown
	 * @throws  IOException
	 *          If an I/O error occurs
	 */
	public static ImproveAsynchronousServerSocketChannel open(ImproveAsynchronousChannelGroup group)
			throws IOException
	{
		ImproveAsynchronousChannelProvider provider = (group == null) ?
				ImproveAsynchronousChannelProvider.provider() : group.provider();
		return provider.openImproveAsynchronousServerSocketChannel(group);
	}

	/**
	 * Opens an asynchronous server-socket channel.
	 *
	 * <p> This method returns an asynchronous server socket channel that is
	 * bound to the <em>default group</em>. This method is equivalent to evaluating
	 * the expression:
	 * <blockquote><pre>
	 * open((AsynchronousChannelGroup)null);
	 * </pre></blockquote>
	 *
	 * @return  A new asynchronous server socket channel
	 *
	 * @throws  IOException
	 *          If an I/O error occurs
	 */
	public static ImproveAsynchronousServerSocketChannel open()
			throws IOException
	{
		return open(null);
	}

	/**
	 * Binds the channel's socket to a local address and configures the socket to
	 * listen for connections.
	 *
	 * <p> An invocation of this method is equivalent to the following:
	 * <blockquote><pre>
	 * bind(local, 0);
	 * </pre></blockquote>
	 *
	 * @param   local
	 *          The local address to bind the socket, or {@code null} to bind
	 *          to an automatically assigned socket address
	 *
	 * @return  This channel
	 *
	 * @throws  AlreadyBoundException               {@inheritDoc}
	 * @throws  UnsupportedAddressTypeException     {@inheritDoc}
	 * @throws  SecurityException                   {@inheritDoc}
	 * @throws  ClosedChannelException              {@inheritDoc}
	 * @throws  IOException                         {@inheritDoc}
	 */
	public final ImproveAsynchronousServerSocketChannel bind(SocketAddress local)
			throws IOException
	{
		return bind(local, 0);
	}

	/**
	 * Binds the channel's socket to a local address and configures the socket to
	 * listen for connections.
	 *
	 * <p> This method is used to establish an association between the socket and
	 * a local address. Once an association is established then the socket remains
	 * bound until the associated channel is closed.
	 *
	 * <p> The {@code backlog} parameter is the maximum number of pending
	 * connections on the socket. Its exact semantics are implementation specific.
	 * In particular, an implementation may impose a maximum length or may choose
	 * to ignore the parameter altogther. If the {@code backlog} parameter has
	 * the value {@code 0}, or a negative value, then an implementation specific
	 * default is used.
	 *
	 * @param   local
	 *          The local address to bind the socket, or {@code null} to bind
	 *          to an automatically assigned socket address
	 * @param   backlog
	 *          The maximum number of pending connections
	 *
	 * @return  This channel
	 *
	 * @throws  AlreadyBoundException
	 *          If the socket is already bound
	 * @throws  UnsupportedAddressTypeException
	 *          If the type of the given address is not supported
	 * @throws  SecurityException
	 *          If a security manager has been installed and its {@link
	 *          SecurityManager#checkListen checkListen} method denies the operation
	 * @throws  ClosedChannelException
	 *          If the channel is closed
	 * @throws  IOException
	 *          If some other I/O error occurs
	 */
	public abstract ImproveAsynchronousServerSocketChannel bind(SocketAddress local, int backlog)
			throws IOException;

	/**
	 * @throws  IllegalArgumentException                {@inheritDoc}
	 * @throws  ClosedChannelException                  {@inheritDoc}
	 * @throws  IOException                             {@inheritDoc}
	 */
	public abstract <T> ImproveAsynchronousServerSocketChannel setOption(SocketOption<T> name, T value)
			throws IOException;

	/**
	 * Accepts a connection.
	 *
	 * <p> This method initiates an asynchronous operation to accept a
	 * connection made to this channel's socket. The {@code handler} parameter is
	 * a completion handler that is invoked when a connection is accepted (or
	 * the operation fails). The result passed to the completion handler is
	 * the {@link AsynchronousSocketChannel} to the new connection.
	 *
	 * <p> When a new connection is accepted then the resulting {@code
	 * AsynchronousSocketChannel} will be bound to the same {@link
	 * AsynchronousChannelGroup} as this channel. If the group is {@link
	 * AsynchronousChannelGroup#isShutdown shutdown} and a connection is accepted,
	 * then the connection is closed, and the operation completes with an {@code
	 * IOException} and cause {@link ShutdownChannelGroupException}.
	 *
	 * <p> To allow for concurrent handling of new connections, the completion
	 * handler is not invoked directly by the initiating thread when a new
	 * connection is accepted immediately (see <a
	 * href="AsynchronousChannelGroup.html#threading">Threading</a>).
	 *
	 * <p> If a security manager has been installed then it verifies that the
	 * address and port number of the connection's remote endpoint are permitted
	 * by the security manager's {@link SecurityManager#checkAccept checkAccept}
	 * method. The permission check is performed with privileges that are restricted
	 * by the calling context of this method. If the permission check fails then
	 * the connection is closed and the operation completes with a {@link
	 * SecurityException}.
	 *
	 * @param   <A>
	 *          The type of the attachment
	 * @param   attachment
	 *          The object to attach to the I/O operation; can be {@code null}
	 * @param   handler
	 *          The handler for consuming the result
	 *
	 * @throws  AcceptPendingException
	 *          If an accept operation is already in progress on this channel
	 * @throws  NotYetBoundException
	 *          If this channel's socket has not yet been bound
	 * @throws  ShutdownChannelGroupException
	 *          If the channel group has terminated
	 */
	public abstract <A> void accept(A attachment,
									CompletionHandler<ImproveAsynchronousSocketChannel,? super A> handler);

	/**
	 * Accepts a connection.
	 *
	 * <p> This method initiates an asynchronous operation to accept a
	 * connection made to this channel's socket. The method behaves in exactly
	 * the same manner as the {@link #accept(Object, CompletionHandler)} method
	 * except that instead of specifying a completion handler, this method
	 * returns a {@code Future} representing the pending result. The {@code
	 * Future}'s {@link Future#get() get} method returns the {@link
	 * AsynchronousSocketChannel} to the new connection on successful completion.
	 *
	 * @return  a {@code Future} object representing the pending result
	 *
	 * @throws  AcceptPendingException
	 *          If an accept operation is already in progress on this channel
	 * @throws  NotYetBoundException
	 *          If this channel's socket has not yet been bound
	 */
	public abstract Future<ImproveAsynchronousSocketChannel> accept();

	/**
	 * {@inheritDoc}
	 * <p>
	 * If there is a security manager set, its {@code checkConnect} method is
	 * called with the local address and {@code -1} as its arguments to see
	 * if the operation is allowed. If the operation is not allowed,
	 * a {@code SocketAddress} representing the
	 * {@link java.net.InetAddress#getLoopbackAddress loopback} address and the
	 * local port of the channel's socket is returned.
	 *
	 * @return  The {@code SocketAddress} that the socket is bound to, or the
	 *          {@code SocketAddress} representing the loopback address if
	 *          denied by the security manager, or {@code null} if the
	 *          channel's socket is not bound
	 *
	 * @throws ClosedChannelException     {@inheritDoc}
	 * @throws  IOException                {@inheritDoc}
	 */
	@Override
	public abstract SocketAddress getLocalAddress() throws IOException;
}
