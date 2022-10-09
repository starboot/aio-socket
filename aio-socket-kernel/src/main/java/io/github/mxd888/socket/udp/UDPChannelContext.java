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
package io.github.mxd888.socket.udp;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.StateMachineEnum;
import io.github.mxd888.socket.core.AioConfig;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.utils.pool.buffer.BufferPage;
import io.github.mxd888.socket.core.WriteBuffer;
import io.github.mxd888.socket.utils.pool.buffer.VirtualBuffer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.function.Consumer;

final class UDPChannelContext extends ChannelContext {

    private final UDPChannel udpChannel;

    private final SocketAddress remote;

    UDPChannelContext(final UDPChannel udpChannel, final SocketAddress remote, BufferPage bufferPage) {
        this.udpChannel = udpChannel;
        this.remote = remote;
        Consumer<WriteBuffer> consumer = var -> {
            VirtualBuffer writeBuffer = var.poll();
            if (writeBuffer != null) {
                this.udpChannel.write(writeBuffer, this);
            }
        };
        setWriteBuffer(bufferPage, consumer, this.udpChannel.config.getWriteBufferSize(), 20);
        this.udpChannel.config.getHandler().stateEvent(this, StateMachineEnum.NEW_CHANNEL, null);
    }

    @Override
    public void signalRead() {
        throw new UnsupportedOperationException();
    }

    @Override
    public VirtualBuffer getReadBuffer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close(boolean immediate) {
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
    public AioConfig getAioConfig() {
        return this.udpChannel.config;
    }

    @Override
    protected void sendPacket(Packet packet) {
        throw  new UnsupportedOperationException("UDPChannelContext don't support use sendRunnable");
    }
}
