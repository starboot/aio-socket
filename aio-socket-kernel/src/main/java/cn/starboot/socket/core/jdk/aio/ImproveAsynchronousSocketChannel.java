package cn.starboot.socket.core.jdk.aio;

import cn.starboot.socket.core.utils.pool.memory.MemoryUnit;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public abstract class ImproveAsynchronousSocketChannel
		implements ImproveAsynchronousByteChannel, NetworkChannel {
	private final ImproveAsynchronousChannelProvider provider;

	/**
	 * Initializes a new instance of this class.
	 *
	 * @param provider The provider that created this channel
	 */
	protected ImproveAsynchronousSocketChannel(ImproveAsynchronousChannelProvider provider)
	{
		this.provider = provider;
	}

	/**
	 * Returns the provider that created this channel.
	 *
	 * @return The provider that created this channel
	 */
	public final ImproveAsynchronousChannelProvider provider()
	{
		return provider;
	}

	/**
	 * Opens an asynchronous socket channel.
	 *
	 * <p> The new channel is created by invoking the {@link
	 * ImproveAsynchronousChannelProvider#openImproveAsynchronousSocketChannel
	 * openImproveAsynchronousSocketChannel} method on the {@link
	 * ImproveAsynchronousChannelProvider} that created the group. If the group parameter
	 * is {@code null} then the resulting channel is created by the system-wide
	 * default provider, and bound to the <em>default group</em>.
	 *
	 * @param group The group to which the newly constructed channel should be bound,
	 *              or {@code null} for the default group
	 * @return A new asynchronous socket channel
	 * @throws ShutdownChannelGroupException If the channel group is shutdown
	 * @throws IOException                   If an I/O error occurs
	 */
	public static ImproveAsynchronousSocketChannel open(ImproveAsynchronousChannelGroup group)
			throws IOException {
		ImproveAsynchronousChannelProvider provider = (group == null) ?
				ImproveAsynchronousChannelProvider.provider() : group.provider();
		return provider.openImproveAsynchronousSocketChannel(group);
	}

	/**
	 * Opens an asynchronous socket channel.
	 *
	 * <p> This method returns an asynchronous socket channel that is bound to
	 * the <em>default group</em>.This method is equivalent to evaluating the
	 * expression:
	 * <blockquote><pre>
	 * open((AsynchronousChannelGroup)null);
	 * </pre></blockquote>
	 *
	 * @return A new asynchronous socket channel
	 * @throws IOException If an I/O error occurs
	 */
	public static ImproveAsynchronousSocketChannel open()
			throws IOException
	{
		return open(null);
	}


	// -- socket options and related --

	/**
	 * @throws ConnectionPendingException      If a connection operation is already in progress on this channel
	 * @throws AlreadyBoundException           {@inheritDoc}
	 * @throws UnsupportedAddressTypeException {@inheritDoc}
	 * @throws ClosedChannelException          {@inheritDoc}
	 * @throws IOException                     {@inheritDoc}
	 * @throws SecurityException               If a security manager has been installed and its
	 *                                         {@link SecurityManager#checkListen checkListen} method denies
	 *                                         the operation
	 */
	@Override
	public abstract ImproveAsynchronousSocketChannel bind(SocketAddress local)
			throws IOException;

	/**
	 * @throws IllegalArgumentException {@inheritDoc}
	 * @throws ClosedChannelException   {@inheritDoc}
	 * @throws IOException              {@inheritDoc}
	 */
	@Override
	public abstract <T> ImproveAsynchronousSocketChannel setOption(SocketOption<T> name, T value)
			throws IOException;

	/**
	 * Shutdown the connection for reading without closing the channel.
	 *
	 * <p> Once shutdown for reading then further reads on the channel will
	 * return {@code -1}, the end-of-stream indication. If the input side of the
	 * connection is already shutdown then invoking this method has no effect.
	 * The effect on an outstanding read operation is system dependent and
	 * therefore not specified. The effect, if any, when there is data in the
	 * socket receive buffer that has not been read, or data arrives subsequently,
	 * is also system dependent.
	 *
	 * @return The channel
	 * @throws NotYetConnectedException If this channel is not yet connected
	 * @throws ClosedChannelException   If this channel is closed
	 * @throws IOException              If some other I/O error occurs
	 */
	public abstract ImproveAsynchronousSocketChannel shutdownInput() throws IOException;

	/**
	 * Shutdown the connection for writing without closing the channel.
	 *
	 * <p> Once shutdown for writing then further attempts to write to the
	 * channel will throw {@link ClosedChannelException}. If the output side of
	 * the connection is already shutdown then invoking this method has no
	 * effect. The effect on an outstanding write operation is system dependent
	 * and therefore not specified.
	 *
	 * @return The channel
	 * @throws NotYetConnectedException If this channel is not yet connected
	 * @throws ClosedChannelException   If this channel is closed
	 * @throws IOException              If some other I/O error occurs
	 */
	public abstract ImproveAsynchronousSocketChannel shutdownOutput() throws IOException;

	// -- state --

	/**
	 * Returns the remote address to which this channel's socket is connected.
	 *
	 * <p> Where the channel is bound and connected to an Internet Protocol
	 * socket address then the return value from this method is of type {@link
	 * java.net.InetSocketAddress}.
	 *
	 * @return The remote address; {@code null} if the channel's socket is not
	 * connected
	 * @throws ClosedChannelException If the channel is closed
	 * @throws IOException            If an I/O error occurs
	 */
	public abstract SocketAddress getRemoteAddress() throws IOException;

	// -- asynchronous operations --

	/**
	 * Connects this channel.
	 *
	 * <p> This method initiates an operation to connect this channel. The
	 * {@code handler} parameter is a completion handler that is invoked when
	 * the connection is successfully established or connection cannot be
	 * established. If the connection cannot be established then the channel is
	 * closed.
	 *
	 * <p> This method performs exactly the same security checks as the {@link
	 * java.net.Socket} class.  That is, if a security manager has been
	 * installed then this method verifies that its {@link
	 * java.lang.SecurityManager#checkConnect checkConnect} method permits
	 * connecting to the address and port number of the given remote endpoint.
	 *
	 * @param <A>        The type of the attachment
	 * @param remote     The remote address to which this channel is to be connected
	 * @param attachment The object to attach to the I/O operation; can be {@code null}
	 * @param handler    The handler for consuming the result
	 * @throws UnresolvedAddressException      If the given remote address is not fully resolved
	 * @throws UnsupportedAddressTypeException If the type of the given remote address is not supported
	 * @throws AlreadyConnectedException       If this channel is already connected
	 * @throws ConnectionPendingException      If a connection operation is already in progress on this channel
	 * @throws ShutdownChannelGroupException   If the channel group has terminated
	 * @throws SecurityException               If a security manager has been installed
	 *                                         and it does not permit access to the given remote endpoint
	 * @see #getRemoteAddress
	 */
	public abstract <A> void connect(SocketAddress remote,
									 A attachment,
									 CompletionHandler<Void, ? super A> handler);

	/**
	 * Connects this channel.
	 *
	 * <p> This method initiates an operation to connect this channel. This
	 * method behaves in exactly the same manner as the {@link
	 * #connect(SocketAddress, Object, CompletionHandler)} method except that
	 * instead of specifying a completion handler, this method returns a {@code
	 * Future} representing the pending result. The {@code Future}'s {@link
	 * Future#get() get} method returns {@code null} on successful completion.
	 *
	 * @param remote The remote address to which this channel is to be connected
	 * @return A {@code Future} object representing the pending result
	 * @throws UnresolvedAddressException      If the given remote address is not fully resolved
	 * @throws UnsupportedAddressTypeException If the type of the given remote address is not supported
	 * @throws AlreadyConnectedException       If this channel is already connected
	 * @throws ConnectionPendingException      If a connection operation is already in progress on this channel
	 * @throws SecurityException               If a security manager has been installed
	 *                                         and it does not permit access to the given remote endpoint
	 */
	public abstract Future<Void> connect(SocketAddress remote);

	/**
	 * Reads a sequence of bytes from this channel into the given buffer.
	 *
	 * <p> This method initiates an asynchronous read operation to read a
	 * sequence of bytes from this channel into the given buffer. The {@code
	 * handler} parameter is a completion handler that is invoked when the read
	 * operation completes (or fails). The result passed to the completion
	 * handler is the number of bytes read or {@code -1} if no bytes could be
	 * read because the channel has reached end-of-stream.
	 *
	 * <p> If a timeout is specified and the timeout elapses before the operation
	 * completes then the operation completes with the exception {@link
	 * InterruptedByTimeoutException}. Where a timeout occurs, and the
	 * implementation cannot guarantee that bytes have not been read, or will not
	 * be read from the channel into the given buffer, then further attempts to
	 * read from the channel will cause an unspecific runtime exception to be
	 * thrown.
	 *
	 * <p> Otherwise this method works in the same manner as the {@link
	 * AsynchronousByteChannel#read(ByteBuffer, Object, CompletionHandler)}
	 * method.
	 *
	 * @param <A>        The type of the attachment
	 * @param function        The buffer into which bytes are to be transferred
	 * @param timeout    The maximum time for the I/O operation to complete
	 * @param unit       The time unit of the {@code timeout} argument
	 * @param attachment The object to attach to the I/O operation; can be {@code null}
	 * @param handler    The handler for consuming the result
	 * @throws IllegalArgumentException      If the buffer is read-only
	 * @throws ReadPendingException          If a read operation is already in progress on this channel
	 * @throws NotYetConnectedException      If this channel is not yet connected
	 * @throws ShutdownChannelGroupException If the channel group has terminated
	 */
	public abstract <A> void read(Function<Boolean,MemoryUnit> function,
								  long timeout,
								  TimeUnit unit,
								  A attachment,
								  CompletionHandler<Integer, ? super A> handler);

	/**
	 * @throws IllegalArgumentException      {@inheritDoc}
	 * @throws ReadPendingException          {@inheritDoc}
	 * @throws NotYetConnectedException      If this channel is not yet connected
	 * @throws ShutdownChannelGroupException If the channel group has terminated
	 */
	@Override
	public final <A> void read(Function<Boolean,MemoryUnit> function,
							   A attachment,
							   CompletionHandler<Integer, ? super A> handler) {
		read(function, 0L, TimeUnit.MILLISECONDS, attachment, handler);
	}

	/**
	 * @throws IllegalArgumentException {@inheritDoc}
	 * @throws ReadPendingException     {@inheritDoc}
	 * @throws NotYetConnectedException If this channel is not yet connected
	 */
	@Override
	public abstract Future<Integer> read(ByteBuffer dst);

	/**
	 * Reads a sequence of bytes from this channel into a subsequence of the
	 * given buffers. This operation, sometimes called a <em>scattering read</em>,
	 * is often useful when implementing network protocols that group data into
	 * segments consisting of one or more fixed-length headers followed by a
	 * variable-length body. The {@code handler} parameter is a completion
	 * handler that is invoked when the read operation completes (or fails). The
	 * result passed to the completion handler is the number of bytes read or
	 * {@code -1} if no bytes could be read because the channel has reached
	 * end-of-stream.
	 *
	 * <p> This method initiates a read of up to <i>r</i> bytes from this channel,
	 * where <i>r</i> is the total number of bytes remaining in the specified
	 * subsequence of the given buffer array, that is,
	 *
	 * <blockquote><pre>
	 * dsts[offset].remaining()
	 *     + dsts[offset+1].remaining()
	 *     + ... + dsts[offset+length-1].remaining()</pre></blockquote>
	 * <p>
	 * at the moment that the read is attempted.
	 *
	 * <p> Suppose that a byte sequence of length <i>n</i> is read, where
	 * {@code 0}&nbsp;{@code <}&nbsp;<i>n</i>&nbsp;{@code <=}&nbsp;<i>r</i>.
	 * Up to the first {@code dsts[offset].remaining()} bytes of this sequence
	 * are transferred into buffer {@code dsts[offset]}, up to the next
	 * {@code dsts[offset+1].remaining()} bytes are transferred into buffer
	 * {@code dsts[offset+1]}, and so forth, until the entire byte sequence
	 * is transferred into the given buffers.  As many bytes as possible are
	 * transferred into each buffer, hence the final position of each updated
	 * buffer, except the last updated buffer, is guaranteed to be equal to
	 * that buffer's limit. The underlying operating system may impose a limit
	 * on the number of buffers that may be used in an I/O operation. Where the
	 * number of buffers (with bytes remaining), exceeds this limit, then the
	 * I/O operation is performed with the maximum number of buffers allowed by
	 * the operating system.
	 *
	 * <p> If a timeout is specified and the timeout elapses before the operation
	 * completes then it completes with the exception {@link
	 * InterruptedByTimeoutException}. Where a timeout occurs, and the
	 * implementation cannot guarantee that bytes have not been read, or will not
	 * be read from the channel into the given buffers, then further attempts to
	 * read from the channel will cause an unspecific runtime exception to be
	 * thrown.
	 *
	 * @param <A>        The type of the attachment
	 * @param dsts       The buffers into which bytes are to be transferred
	 * @param offset     The offset within the buffer array of the first buffer into which
	 *                   bytes are to be transferred; must be non-negative and no larger than
	 *                   {@code dsts.length}
	 * @param length     The maximum number of buffers to be accessed; must be non-negative
	 *                   and no larger than {@code dsts.length - offset}
	 * @param timeout    The maximum time for the I/O operation to complete
	 * @param unit       The time unit of the {@code timeout} argument
	 * @param attachment The object to attach to the I/O operation; can be {@code null}
	 * @param handler    The handler for consuming the result
	 * @throws IndexOutOfBoundsException     If the pre-conditions for the {@code offset}  and {@code length}
	 *                                       parameter aren't met
	 * @throws IllegalArgumentException      If the buffer is read-only
	 * @throws ReadPendingException          If a read operation is already in progress on this channel
	 * @throws NotYetConnectedException      If this channel is not yet connected
	 * @throws ShutdownChannelGroupException If the channel group has terminated
	 */
	public abstract <A> void read(ByteBuffer[] dsts,
								  int offset,
								  int length,
								  long timeout,
								  TimeUnit unit,
								  A attachment,
								  CompletionHandler<Long, ? super A> handler);

	/**
	 * Writes a sequence of bytes to this channel from the given buffer.
	 *
	 * <p> This method initiates an asynchronous write operation to write a
	 * sequence of bytes to this channel from the given buffer. The {@code
	 * handler} parameter is a completion handler that is invoked when the write
	 * operation completes (or fails). The result passed to the completion
	 * handler is the number of bytes written.
	 *
	 * <p> If a timeout is specified and the timeout elapses before the operation
	 * completes then it completes with the exception {@link
	 * InterruptedByTimeoutException}. Where a timeout occurs, and the
	 * implementation cannot guarantee that bytes have not been written, or will
	 * not be written to the channel from the given buffer, then further attempts
	 * to write to the channel will cause an unspecific runtime exception to be
	 * thrown.
	 *
	 * <p> Otherwise this method works in the same manner as the {@link
	 * AsynchronousByteChannel#write(ByteBuffer, Object, CompletionHandler)}
	 * method.
	 *
	 * @param <A>        The type of the attachment
	 * @param src        The buffer from which bytes are to be retrieved
	 * @param timeout    The maximum time for the I/O operation to complete
	 * @param unit       The time unit of the {@code timeout} argument
	 * @param attachment The object to attach to the I/O operation; can be {@code null}
	 * @param handler    The handler for consuming the result
	 * @throws WritePendingException         If a write operation is already in progress on this channel
	 * @throws NotYetConnectedException      If this channel is not yet connected
	 * @throws ShutdownChannelGroupException If the channel group has terminated
	 */
	public abstract <A> void write(MemoryUnit src,
								   long timeout,
								   TimeUnit unit,
								   A attachment,
								   CompletionHandler<Integer, ? super A> handler);

	/**
	 * @throws WritePendingException         {@inheritDoc}
	 * @throws NotYetConnectedException      If this channel is not yet connected
	 * @throws ShutdownChannelGroupException If the channel group has terminated
	 */
	@Override
	public final <A> void write(MemoryUnit src,
								A attachment,
								CompletionHandler<Integer, ? super A> handler) {
		write(src, 0L, TimeUnit.MILLISECONDS, attachment, handler);
	}

	/**
	 * @throws WritePendingException    {@inheritDoc}
	 * @throws NotYetConnectedException If this channel is not yet connected
	 */
	@Override
	public abstract Future<Integer> write(ByteBuffer src);

	/**
	 * Writes a sequence of bytes to this channel from a subsequence of the given
	 * buffers. This operation, sometimes called a <em>gathering write</em>, is
	 * often useful when implementing network protocols that group data into
	 * segments consisting of one or more fixed-length headers followed by a
	 * variable-length body. The {@code handler} parameter is a completion
	 * handler that is invoked when the write operation completes (or fails).
	 * The result passed to the completion handler is the number of bytes written.
	 *
	 * <p> This method initiates a write of up to <i>r</i> bytes to this channel,
	 * where <i>r</i> is the total number of bytes remaining in the specified
	 * subsequence of the given buffer array, that is,
	 *
	 * <blockquote><pre>
	 * srcs[offset].remaining()
	 *     + srcs[offset+1].remaining()
	 *     + ... + srcs[offset+length-1].remaining()</pre></blockquote>
	 * <p>
	 * at the moment that the write is attempted.
	 *
	 * <p> Suppose that a byte sequence of length <i>n</i> is written, where
	 * {@code 0}&nbsp;{@code <}&nbsp;<i>n</i>&nbsp;{@code <=}&nbsp;<i>r</i>.
	 * Up to the first {@code srcs[offset].remaining()} bytes of this sequence
	 * are written from buffer {@code srcs[offset]}, up to the next
	 * {@code srcs[offset+1].remaining()} bytes are written from buffer
	 * {@code srcs[offset+1]}, and so forth, until the entire byte sequence is
	 * written.  As many bytes as possible are written from each buffer, hence
	 * the final position of each updated buffer, except the last updated
	 * buffer, is guaranteed to be equal to that buffer's limit. The underlying
	 * operating system may impose a limit on the number of buffers that may be
	 * used in an I/O operation. Where the number of buffers (with bytes
	 * remaining), exceeds this limit, then the I/O operation is performed with
	 * the maximum number of buffers allowed by the operating system.
	 *
	 * <p> If a timeout is specified and the timeout elapses before the operation
	 * completes then it completes with the exception {@link
	 * InterruptedByTimeoutException}. Where a timeout occurs, and the
	 * implementation cannot guarantee that bytes have not been written, or will
	 * not be written to the channel from the given buffers, then further attempts
	 * to write to the channel will cause an unspecific runtime exception to be
	 * thrown.
	 *
	 * @param <A>        The type of the attachment
	 * @param srcs       The buffers from which bytes are to be retrieved
	 * @param offset     The offset within the buffer array of the first buffer from which
	 *                   bytes are to be retrieved; must be non-negative and no larger
	 *                   than {@code srcs.length}
	 * @param length     The maximum number of buffers to be accessed; must be non-negative
	 *                   and no larger than {@code srcs.length - offset}
	 * @param timeout    The maximum time for the I/O operation to complete
	 * @param unit       The time unit of the {@code timeout} argument
	 * @param attachment The object to attach to the I/O operation; can be {@code null}
	 * @param handler    The handler for consuming the result
	 * @throws IndexOutOfBoundsException     If the pre-conditions for the {@code offset}  and {@code length}
	 *                                       parameter aren't met
	 * @throws WritePendingException         If a write operation is already in progress on this channel
	 * @throws NotYetConnectedException      If this channel is not yet connected
	 * @throws ShutdownChannelGroupException If the channel group has terminated
	 */
	public abstract <A> void write(ByteBuffer[] srcs,
								   int offset,
								   int length,
								   long timeout,
								   TimeUnit unit,
								   A attachment,
								   CompletionHandler<Long, ? super A> handler);

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
	 * @return The {@code SocketAddress} that the socket is bound to, or the
	 * {@code SocketAddress} representing the loopback address if
	 * denied by the security manager, or {@code null} if the
	 * channel's socket is not bound
	 * @throws ClosedChannelException {@inheritDoc}
	 * @throws IOException            {@inheritDoc}
	 */
	public abstract SocketAddress getLocalAddress() throws IOException;
}
