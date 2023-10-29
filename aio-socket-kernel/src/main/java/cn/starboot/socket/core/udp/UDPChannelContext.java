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
package cn.starboot.socket.core.udp;

import cn.starboot.socket.core.Packet;
import cn.starboot.socket.core.enums.StateMachineEnum;
import cn.starboot.socket.core.Aio;
import cn.starboot.socket.core.AioConfig;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.exception.AioEncoderException;
import cn.starboot.socket.core.AsyAioWorker;
import cn.starboot.socket.core.jdk.nio.NioEventLoopWorker;
import cn.starboot.socket.core.utils.concurrent.collection.ConcurrentWithList;
import cn.starboot.socket.core.utils.pool.memory.MemoryBlock;
import cn.starboot.socket.core.utils.pool.memory.MemoryUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.function.Consumer;

final class UDPChannelContext extends ChannelContext {

	private static final Logger LOGGER = LoggerFactory.getLogger(UDPChannelContext.class);

	private final DatagramChannel datagramChannel;

	private final SocketAddress remote;

	private final AioConfig config;

	private final NioEventLoopWorker nioEventLoopWorker;

	/**
	 * 存放待发送的完整比特流
	 */
	private MemoryUnit writeMemoryUnit;

	private final ConcurrentWithList<MemoryUnit> concurrentWithList = new ConcurrentWithList<>(new ArrayList<>());

	UDPChannelContext(
			DatagramChannel datagramChannel,
			final AioConfig aioConfig,
			SocketAddress remote,
			MemoryBlock memoryBlock,
			NioEventLoopWorker nioEventLoopWorker) {
		this.datagramChannel = datagramChannel;
		this.remote = remote;
		this.config = aioConfig;
		this.nioEventLoopWorker = nioEventLoopWorker;
		initUDPChannelContext(memoryBlock);
	}

	private void initUDPChannelContext(MemoryBlock memoryBlock) {
		setWriteBuffer(
				memoryBlock,
				memoryUnit -> {
					MemoryUnit writeUnit = memoryUnit.poll();
					if (writeUnit != null) {
						this.writeMemoryUnit = writeUnit;
						doWrite();
					}
				},
				getAioConfig().getWriteBufferSize(),
				16);
		getAioConfig().getHandler().stateEvent(this, StateMachineEnum.NEW_CHANNEL, null);
	}

	UDPChannelContext addMemoryUnit(MemoryUnit readMemoryUnit) {
		if (concurrentWithList.add(readMemoryUnit)) {
			return this;
		}
		if (LOGGER.isErrorEnabled()) {
			LOGGER.error("存入失败");
		}
		return this;
	}

	void handle() {

		// 处理
	}

	void doWrite() {
		// 写
		try {
			int send = datagramChannel.send(writeMemoryUnit.buffer(), remote);

			if (send > 0) {
				System.out.println("写入成功");
			}

			if (send == 0) {
				// 无法写
				nioEventLoopWorker.addRegister(new Consumer<Selector>() {
					@Override
					public void accept(Selector selector) {
						try {
							datagramChannel.register(selector, SelectionKey.OP_WRITE, this);
						} catch (ClosedChannelException closedChannelException) {
							closedChannelException.printStackTrace();
						}
					}
				});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void signalRead(boolean flip) {
		throw new UnsupportedOperationException();
	}

	@Override
	public MemoryUnit getReadBuffer() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close(boolean immediate) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("The UDP channel with ID " + this.getId() + " is closing");
		}
//		this.udpChannel.close();
		this.byteBuf.close();
	}

	@Override
	public InetSocketAddress getLocalAddress() throws IOException {
//		return (InetSocketAddress) udpChannel.getChannel().getLocalAddress();
		return (InetSocketAddress) datagramChannel.getLocalAddress();
	}

	@Override
	public InetSocketAddress getRemoteAddress() {
		return (InetSocketAddress) remote;
	}

	@Override
	protected AsyAioWorker getAioWorker() {
		throw new UnsupportedOperationException();
	}

	@Override
	public AioConfig getAioConfig() {
		return config;
	}

	@Override
	protected boolean aioEncoder(Packet packet, boolean isBlock, boolean isFlush) {
		try {
			synchronized (this) {
				getAioConfig().getHandler().encode(packet, this);
			}
		} catch (AioEncoderException e) {
			Aio.close(this);
			return false;
		}
		flush(false);
		return true;
	}

	@Override
	public void awaitRead() {
		throw new UnsupportedOperationException();
	}

	protected void UDPFlush() {
		flush(false);
	}
}
