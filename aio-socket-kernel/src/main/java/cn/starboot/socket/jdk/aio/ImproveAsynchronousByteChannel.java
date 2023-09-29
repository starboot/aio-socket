package cn.starboot.socket.jdk.aio;

import cn.starboot.socket.utils.pool.memory.MemoryUnit;

import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public interface ImproveAsynchronousByteChannel
		extends AsynchronousChannel
{
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
	 * <p> The read operation may read up to <i>r</i> bytes from the channel,
	 * where <i>r</i> is the number of bytes remaining in the buffer, that is,
	 * {@code dst.remaining()} at the time that the read is attempted. Where
	 * <i>r</i> is 0, the read operation completes immediately with a result of
	 * {@code 0} without initiating an I/O operation.
	 *
	 * <p> Suppose that a byte sequence of length <i>n</i> is read, where
	 * <tt>0</tt>&nbsp;<tt>&lt;</tt>&nbsp;<i>n</i>&nbsp;<tt>&lt;=</tt>&nbsp;<i>r</i>.
	 * This byte sequence will be transferred into the buffer so that the first
	 * byte in the sequence is at index <i>p</i> and the last byte is at index
	 * <i>p</i>&nbsp;<tt>+</tt>&nbsp;<i>n</i>&nbsp;<tt>-</tt>&nbsp;<tt>1</tt>,
	 * where <i>p</i> is the buffer's position at the moment the read is
	 * performed. Upon completion the buffer's position will be equal to
	 * <i>p</i>&nbsp;<tt>+</tt>&nbsp;<i>n</i>; its limit will not have changed.
	 *
	 * <p> Buffers are not safe for use by multiple concurrent threads so care
	 * should be taken to not access the buffer until the operation has
	 * completed.
	 *
	 * <p> This method may be invoked at any time. Some channel types may not
	 * allow more than one read to be outstanding at any given time. If a thread
	 * initiates a read operation before a previous read operation has
	 * completed then a {@link ReadPendingException} will be thrown.
	 *
	 * @param   <A>
	 *          The type of the attachment
	 * @param   supplier
	 *          The buffer into which bytes are to be transferred
	 * @param   attachment
	 *          The object to attach to the I/O operation; can be {@code null}
	 * @param   handler
	 *          The completion handler
	 *
	 * @throws  IllegalArgumentException
	 *          If the buffer is read-only
	 * @throws  ReadPendingException
	 *          If the channel does not allow more than one read to be outstanding
	 *          and a previous read has not completed
	 * @throws ShutdownChannelGroupException
	 *          If the channel is associated with a {@link AsynchronousChannelGroup
	 *          group} that has terminated
	 */
	<A> void read(Supplier<MemoryUnit> supplier,
						A attachment,
						CompletionHandler<Integer,? super A> handler);

	/**
	 * Reads a sequence of bytes from this channel into the given buffer.
	 *
	 * <p> This method initiates an asynchronous read operation to read a
	 * sequence of bytes from this channel into the given buffer. The method
	 * behaves in exactly the same manner as the {@link
	 * #read(Supplier,Object,CompletionHandler)
	 * read(ByteBuffer,Object,CompletionHandler)} method except that instead
	 * of specifying a completion handler, this method returns a {@code Future}
	 * representing the pending result. The {@code Future}'s {@link Future#get()
	 * get} method returns the number of bytes read or {@code -1} if no bytes
	 * could be read because the channel has reached end-of-stream.
	 *
	 * @param   dst
	 *          The buffer into which bytes are to be transferred
	 *
	 * @return  A Future representing the result of the operation
	 *
	 * @throws  IllegalArgumentException
	 *          If the buffer is read-only
	 * @throws  ReadPendingException
	 *          If the channel does not allow more than one read to be outstanding
	 *          and a previous read has not completed
	 */
	Future<Integer> read(ByteBuffer dst);

	/**
	 * Writes a sequence of bytes to this channel from the given buffer.
	 *
	 * <p> This method initiates an asynchronous write operation to write a
	 * sequence of bytes to this channel from the given buffer. The {@code
	 * handler} parameter is a completion handler that is invoked when the write
	 * operation completes (or fails). The result passed to the completion
	 * handler is the number of bytes written.
	 *
	 * <p> The write operation may write up to <i>r</i> bytes to the channel,
	 * where <i>r</i> is the number of bytes remaining in the buffer, that is,
	 * {@code src.remaining()} at the time that the write is attempted. Where
	 * <i>r</i> is 0, the write operation completes immediately with a result of
	 * {@code 0} without initiating an I/O operation.
	 *
	 * <p> Suppose that a byte sequence of length <i>n</i> is written, where
	 * <tt>0</tt>&nbsp;<tt>&lt;</tt>&nbsp;<i>n</i>&nbsp;<tt>&lt;=</tt>&nbsp;<i>r</i>.
	 * This byte sequence will be transferred from the buffer starting at index
	 * <i>p</i>, where <i>p</i> is the buffer's position at the moment the
	 * write is performed; the index of the last byte written will be
	 * <i>p</i>&nbsp;<tt>+</tt>&nbsp;<i>n</i>&nbsp;<tt>-</tt>&nbsp;<tt>1</tt>.
	 * Upon completion the buffer's position will be equal to
	 * <i>p</i>&nbsp;<tt>+</tt>&nbsp;<i>n</i>; its limit will not have changed.
	 *
	 * <p> Buffers are not safe for use by multiple concurrent threads so care
	 * should be taken to not access the buffer until the operation has
	 * completed.
	 *
	 * <p> This method may be invoked at any time. Some channel types may not
	 * allow more than one write to be outstanding at any given time. If a thread
	 * initiates a write operation before a previous write operation has
	 * completed then a {@link WritePendingException} will be thrown.
	 *
	 * @param   <A>
	 *          The type of the attachment
	 * @param   src
	 *          The buffer from which bytes are to be retrieved
	 * @param   attachment
	 *          The object to attach to the I/O operation; can be {@code null}
	 * @param   handler
	 *          The completion handler object
	 *
	 * @throws  WritePendingException
	 *          If the channel does not allow more than one write to be outstanding
	 *          and a previous write has not completed
	 * @throws  ShutdownChannelGroupException
	 *          If the channel is associated with a {@link AsynchronousChannelGroup
	 *          group} that has terminated
	 */
	<A> void write(MemoryUnit src,
				   A attachment,
				   CompletionHandler<Integer,? super A> handler);

	/**
	 * Writes a sequence of bytes to this channel from the given buffer.
	 *
	 * <p> This method initiates an asynchronous write operation to write a
	 * sequence of bytes to this channel from the given buffer. The method
	 * behaves in exactly the same manner as the {@link
	 * #write(MemoryUnit,Object,CompletionHandler)
	 * write(ByteBuffer,Object,CompletionHandler)} method except that instead
	 * of specifying a completion handler, this method returns a {@code Future}
	 * representing the pending result. The {@code Future}'s {@link Future#get()
	 * get} method returns the number of bytes written.
	 *
	 * @param   src
	 *          The buffer from which bytes are to be retrieved
	 *
	 * @return A Future representing the result of the operation
	 *
	 * @throws  WritePendingException
	 *          If the channel does not allow more than one write to be outstanding
	 *          and a previous write has not completed
	 */
	Future<Integer> write(ByteBuffer src);
}
