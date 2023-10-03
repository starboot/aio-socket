/*
 *    Copyright 2019 The aio-socket Project
 *
 *    The aio-socket Project Licenses this file to you under the Apache License,
 *    Version 2.0 (the "License"); you may not use this file except in compliance
 *    with the License. You may obtain a copy of the License at:
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package cn.starboot.socket.core;

import cn.starboot.socket.enums.ChannelStatusEnum;
import cn.starboot.socket.exception.AioEncoderException;
import cn.starboot.socket.jdk.aio.ApplyAndRegister;
import cn.starboot.socket.jdk.aio.ImproveAsynchronousSocketChannel;
import cn.starboot.socket.plugins.AbstractPlugin;
import cn.starboot.socket.utils.pool.memory.MemoryBlock;
import cn.starboot.socket.Monitor;
import cn.starboot.socket.Packet;
import cn.starboot.socket.enums.StateMachineEnum;
import cn.starboot.socket.exception.AioDecoderException;
import cn.starboot.socket.utils.pool.memory.MemoryUnit;
import cn.starboot.socket.intf.Handler;
import cn.starboot.socket.utils.AIOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 通道上下文信息类
 * socket发起连接和读写相关方法（全是异步操作） https://blog.csdn.net/weixin_45754452/article/details/121925936
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
final class TCPChannelContext extends ChannelContext {

	private final Lock lock = new ReentrantLock();

	/**
	 * 底层通信channel对象
	 */
	private final ImproveAsynchronousSocketChannel channel;

	/**
	 * 输出信号量,防止并发write导致异常
	 */
	private final Semaphore semaphore = new Semaphore(1);

	/**
	 * 读回调
	 */
	private final ReadCompletionHandler readCompletionHandler;

	/**
	 * 写回调
	 */
	private final WriteCompletionHandler writeCompletionHandler;

	/**
	 * 服务配置
	 */
	private final AioConfig aioConfig;

	/**
	 * 是否读通道以至末尾; 以后可以用于判断该链接是否为攻击连接，是的话将其关闭并且拒绝此IP连接
	 */
	boolean eof;

	/**
	 * 同步等待数量
	 */
	int modCount = 0;

	/**
	 * 同步输入流
	 */
	private InputStream inputStream;

	/**
	 * 存放刚读到的数据
	 */
	private MemoryUnit readBuffer;

	/**
	 * 存放待发送的完整比特流
	 */
	private MemoryUnit writeBuffer;

	/**
	 * 消息解码逻辑执行器
	 */
	private AsyAioWorker aioWorker;

	/**
	 * 构造通道上下文对象
	 *
	 * @param channel                Socket通道
	 * @param config                 配置项
	 * @param readCompletionHandler  读回调
	 * @param writeCompletionHandler 写回调
	 * @param memoryBlock            绑定内存页
	 */
	TCPChannelContext(ImproveAsynchronousSocketChannel channel,
					  final AioConfig config,
					  ReadCompletionHandler readCompletionHandler,
					  WriteCompletionHandler writeCompletionHandler,
					  MemoryBlock memoryBlock) {
		this.channel = channel;
		this.readCompletionHandler = readCompletionHandler;
		this.writeCompletionHandler = writeCompletionHandler;
		this.aioConfig = config;

		// Java8 函数式编程的无返回函数
		Consumer<WriteBuffer> flushConsumer = var -> {
			if (!semaphore.tryAcquire()) {
				return;
			}
			this.writeBuffer = var.poll();
			if (writeBuffer == null) {
				semaphore.release();
			} else {
				continueWrite(writeBuffer);
			}
		};
		// 为当前ChannelContext添加对外输出流
		setWriteBuffer(memoryBlock, flushConsumer, getAioConfig().getWriteBufferSize(), 16);
		// 触发状态机
		getAioConfig().getHandler().stateEvent(this, StateMachineEnum.NEW_CHANNEL, null);
	}

	private Supplier<MemoryUnit> readSupplier;
	/**
	 * 初始化TCPChannelContext
	 */
	void initTCPChannelContext(Supplier<MemoryUnit> supplier) {

//		readSupplier = ((ApplyAndRegister<MemoryUnit>) supplier::get).andRegister(memoryUnit -> readBuffer = memoryUnit);

		readSupplier = () -> { readBuffer = supplier.get(); return readBuffer; };

//		this.readBuffer = supplier.get();
//		this.readBuffer.buffer().flip();
//		signalRead(false);
		continueRead();
	}

	/**
	 * 触发通道的读回调操作
	 */
	@Override
	public void signalRead(boolean isFlip) {
		int modCount = this.modCount;

		flipRead(isFlip);
		if (status == ChannelStatusEnum.CHANNEL_STATUS_CLOSED) {
			return;
		}
		final ByteBuffer readBuffer = this.readBuffer.buffer();
		final Handler handler = getAioConfig().getHandler();
		while (readBuffer.hasRemaining() && status == ChannelStatusEnum.CHANNEL_STATUS_ENABLED) {
//			Packet packet = null;
//			try {
//				if (getOldByteBuffer().isEmpty()) {
//					packet = handler.decode(this.readBuffer, this);
//				} else {
//					getOldByteBuffer().offer(this.readBuffer);
//					packet = handler.decode(getOldByteBuffer().peek(), this);
//				}
//			} catch (AioDecoderException e) {
//				handler.stateEvent(this, StateMachineEnum.DECODE_EXCEPTION, e);
//				e.printStackTrace();
//			}
//			if (packet == null) {
//				break;
//			}
//			aioHandler(packet);
//			if (modCount != this.modCount) {
//				return;
//			}

			byte[] aByte = getByte(this.readBuffer);
			if (aByte == null) {
				break;
			}
			try {
				byteBuf.writeInt(aByte.length);
				byteBuf.write(aByte);
			} catch (AioEncoderException e) {
				e.printStackTrace();
			}

		}
		flush();
		if (eof || status == ChannelStatusEnum.CHANNEL_STATUS_CLOSING) {
			close(false);
			handler.stateEvent(this, StateMachineEnum.INPUT_SHUTDOWN, null);
			return;
		}
		if (status == ChannelStatusEnum.CHANNEL_STATUS_CLOSED) {
			return;
		}
		if (readBuffer.capacity() == readBuffer.remaining()) {
			// buffer 满了
			if (getOldByteBuffer().isFull()) {
				RuntimeException exception = new RuntimeException("readBuffer queue has overflow");
				handler.stateEvent(this, StateMachineEnum.DECODE_EXCEPTION, exception);
				throw exception;
			}
			if (getOldByteBuffer().isEmpty()) {
				// 空间太小，申请一份空间继续读
				getOldByteBuffer().offer(this.readBuffer);
			}
			this.readBuffer = getVirtualBuffer(getAioConfig().getReadBufferSize());
			this.readBuffer.buffer().clear();
		} else {
//			System.out.println("回味...");
			readBuffer.compact();
		}
//		System.out.println("继续读..." + readBuffer.position() + "--" + readBuffer.limit() + " **** " + readBuffer.capacity());
		continueRead();
	}

	private byte[] getByte(MemoryUnit memoryUnit) {
		ByteBuffer buffer = memoryUnit.buffer();
		int remaining = buffer.remaining();
		if (remaining < Integer.BYTES) {
			return null;
		}
		buffer.mark();
		int length = buffer.getInt();
		if (buffer.remaining() < length) {
			buffer.reset();
			return null;
		}
		byte[] b = AIOUtil.getBytesFromByteBuffer(memoryUnit, length, Integer.BYTES, this);
		if (b == null) {
			buffer.reset();
			return null;
		}
		return b;
	}

	/**
	 * 触发通道读方法
	 *
	 */
	private void continueRead() {
		Monitor monitor = getAioConfig().getMonitor();
		if (monitor != null) {
			monitor.beforeRead(this);
		}
		channel.read(readSupplier, this, readCompletionHandler);
	}

	/**
	 * 触发AIO的写操作,
	 * 需要调用控制同步
	 */
	void writeCompleted() {
		if (writeBuffer == null) {
			writeBuffer = byteBuf.poll();
		} else if (!writeBuffer.buffer().hasRemaining()) {
			writeBuffer.clean();
			writeBuffer = byteBuf.poll();
		}
		if (writeBuffer != null) {
			continueWrite(writeBuffer);
			return;
		}
		semaphore.release();
		//此时可能是Closing或Closed状态
		if (status != ChannelStatusEnum.CHANNEL_STATUS_ENABLED) {
			close();
		} else {
			//也许此时有新的消息通过write方法添加到writeCacheQueue中
			flush();
		}
	}

	/**
	 * 触发写操作
	 *
	 * @param writeBuffer 存放待输出数据的buffer
	 */
	private void continueWrite(MemoryUnit writeBuffer) {
		Monitor monitor = getAioConfig().getMonitor();
		if (monitor != null) {
			monitor.beforeWrite(this);
		}
		channel.write(writeBuffer, this, writeCompletionHandler);
	}

	private void flipRead(boolean eof) {
		this.eof = eof;
		this.readBuffer.buffer().flip();
	}

	/**
	 * 断言当前会话是否可用
	 *
	 * @throws IOException IO异常
	 */
	private void assertChannel() throws IOException {
		if (status == ChannelStatusEnum.CHANNEL_STATUS_CLOSED || channel == null) {
			throw new IOException("ChannelContext is closed");
		}
	}

	/**
	 * 调用处理器
	 *
	 * @param packet 消息包
	 */
	private void aioHandler(Packet packet) {
		Packet handle = getAioConfig().getHandler().handle(this, packet);
		if (handle != null) {
			aioEncoder(handle, false, false);
		}
	}

	@Override
	public synchronized void close(boolean immediate) {
		if (status == ChannelStatusEnum.CHANNEL_STATUS_CLOSED) {
			return;
		}
		status = immediate ? ChannelStatusEnum.CHANNEL_STATUS_CLOSED : ChannelStatusEnum.CHANNEL_STATUS_CLOSING;
		if (immediate) {
			try {
				this.byteBuf.close();
				if (readBuffer != null) {
					readBuffer.clean();
					readBuffer = null;
				}
				if (writeBuffer != null) {
					writeBuffer.clean();
					writeBuffer = null;
				}
			} finally {
				AIOUtil.close(channel);
				getAioConfig().getHandler().stateEvent(this, StateMachineEnum.CHANNEL_CLOSED, null);
			}
		} else if ((writeBuffer == null || !writeBuffer.buffer().hasRemaining()) && byteBuf.isEmpty()) {
			close(true);
		} else {
			getAioConfig().getHandler().stateEvent(this, StateMachineEnum.CHANNEL_CLOSING, null);
			flush();
		}
	}

	@Override
	public final InetSocketAddress getLocalAddress() throws IOException {
		assertChannel();
		return (InetSocketAddress) channel.getLocalAddress();
	}

	@Override
	public final InetSocketAddress getRemoteAddress() throws IOException {
		assertChannel();
		return (InetSocketAddress) channel.getRemoteAddress();
	}

	@Override
	protected AsyAioWorker getAioWorker() {
		return this.aioWorker;
	}

	@Override
	public AioConfig getAioConfig() {
		return this.aioConfig;
	}

	@Override
	protected boolean aioEncoder(Packet packet, boolean isBlock, boolean isFlush) {
		if (isInvalid()) {
			return false;
		}
		try {
			lock.lock();
//			synchronized (this) {
				getAioConfig().getHandler().encode(packet, this);
//			}
//			flush();
		} catch (AioEncoderException e) {
			Aio.close(this);
			return false;
		} finally {
			lock.unlock();
		}
		if (isFlush) {
//			flush();
		}
		return true;
	}

	@Override
	public MemoryUnit getReadBuffer() {
		return this.readBuffer;
	}

	@Override
	public void awaitRead() {
		modCount++;
	}

	/**
	 * 同步读取数据
	 */
	private int synRead() throws IOException {
		ByteBuffer buffer = readBuffer.buffer();
		if (buffer.remaining() > 0) {
			return 0;
		}
		try {
			buffer.clear();
			int size = channel.read(buffer).get();
			buffer.flip();
			return size;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	/**
	 * 获得数据输入流对象。
	 * <p>
	 * faster模式下调用该方法会触发UnsupportedOperationException异常。
	 * </p>
	 * <p>
	 * MessageProcessor采用异步处理消息的方式时，调用该方法可能会出现异常。
	 * </p>
	 *
	 * @return 同步读操作的流对象
	 * @throws IOException io异常
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		return inputStream == null ? getInputStream(-1) : inputStream;
	}

	/**
	 * 获取已知长度的InputStream
	 *
	 * @param length InputStream长度
	 * @return 同步读操作的流对象
	 * @throws IOException io异常
	 */
	@Override
	public InputStream getInputStream(int length) throws IOException {
		if (inputStream != null) {
			throw new IOException("pre inputStream has not closed");
		}
		synchronized (this) {
			if (inputStream == null) {
				inputStream = new InnerInputStream(length);
			}
		}
		return inputStream;
	}

	/**
	 * 同步读操作的InputStream
	 */
	private class InnerInputStream extends InputStream {
		/**
		 * 当前InputSteam可读字节数
		 */
		private int remainLength;

		InnerInputStream(int length) {
			this.remainLength = length >= 0 ? length : -1;
		}

		@Override
		public int read() throws IOException {
			if (remainLength == 0) {
				return -1;
			}
			ByteBuffer readBuffer = TCPChannelContext.this.readBuffer.buffer();
			if (readBuffer.hasRemaining()) {
				remainLength--;
				return readBuffer.get();
			}
			if (synRead() == -1) {
				remainLength = 0;
			}
			return read();
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			if (b == null) {
				throw new NullPointerException();
			} else if (off < 0 || len < 0 || len > b.length - off) {
				throw new IndexOutOfBoundsException();
			} else if (len == 0) {
				return 0;
			}
			if (remainLength == 0) {
				return -1;
			}
			if (remainLength > 0 && remainLength < len) {
				len = remainLength;
			}
			ByteBuffer readBuffer = TCPChannelContext.this.readBuffer.buffer();
			int size = 0;
			while (len > 0 && synRead() != -1) {
				int readSize = Math.min(readBuffer.remaining(), len);
				readBuffer.get(b, off + size, readSize);
				size += readSize;
				len -= readSize;
			}
			remainLength -= size;
			return size;
		}

		@Override
		public int available() throws IOException {
			if (remainLength == 0) {
				return 0;
			}
			if (synRead() == -1) {
				remainLength = 0;
				return remainLength;
			}
			ByteBuffer readBuffer = TCPChannelContext.this.readBuffer.buffer();
			if (remainLength < -1) {
				return readBuffer.remaining();
			} else {
				return Math.min(remainLength, readBuffer.remaining());
			}
		}

		@Override
		public void close() {
			if (TCPChannelContext.this.inputStream == InnerInputStream.this) {
				TCPChannelContext.this.inputStream = null;
			}
		}
	}

}
