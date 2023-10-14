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
package cn.starboot.socket.udp;

import cn.starboot.socket.Packet;
import cn.starboot.socket.enums.StateMachineEnum;
import cn.starboot.socket.core.Aio;
import cn.starboot.socket.core.AioConfig;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.exception.AioEncoderException;
import cn.starboot.socket.core.AsyAioWorker;
import cn.starboot.socket.utils.pool.memory.MemoryBlock;
import cn.starboot.socket.core.WriteBuffer;
import cn.starboot.socket.utils.pool.memory.MemoryUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.function.Consumer;

final class UDPChannelContext extends ChannelContext {

	private static final Logger LOGGER = LoggerFactory.getLogger(UDPChannelContext.class);

	private final UDPChannel udpChannel;

	private final SocketAddress remote;

	UDPChannelContext(UDPChannel udpChannel, SocketAddress remote, MemoryBlock memoryBlock) {
		this.udpChannel = udpChannel;
		this.remote = remote;
		// 为当前ChannelContext添加对外输出流
		setWriteBuffer(memoryBlock,
				buffer -> {
					MemoryUnit writeBuffer = buffer.poll();
					if (writeBuffer != null) {
						this.udpChannel.write(writeBuffer, this);
					}
				},
				getAioConfig().getWriteBufferSize(),
				16);
		getAioConfig().getHandler().stateEvent(this, StateMachineEnum.NEW_CHANNEL, null);
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
		this.udpChannel.close();
		this.byteBuf.close();
	}

	@Override
	public InetSocketAddress getLocalAddress() throws IOException {
		return (InetSocketAddress) udpChannel.getChannel().getLocalAddress();
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
		return this.udpChannel.config;
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
