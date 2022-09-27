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

    private final WriteBuffer byteBuf;

    UDPChannelContext(final UDPChannel udpChannel, final SocketAddress remote, BufferPage bufferPage) {
        this.udpChannel = udpChannel;
        this.remote = remote;
        Consumer<WriteBuffer> consumer = var -> {
            VirtualBuffer writeBuffer = var.poll();
            if (writeBuffer != null) {
                udpChannel.write(writeBuffer, UDPChannelContext.this);
            }
        };
        this.byteBuf = new WriteBuffer(bufferPage, consumer, udpChannel.config.getWriteBufferSize());
        udpChannel.config.getHandler().stateEvent(this, StateMachineEnum.NEW_CHANNEL, null);
    }

    public void awaitRead() {
        throw new UnsupportedOperationException();
    }

    public void signalRead() {
        throw new UnsupportedOperationException();
    }

    @Override
    public VirtualBuffer getVirtualBuffer(int len) {
        return byteBuf.newVirtualBuffer(len);
    }

    @Override
    public WriteBuffer getWriteBuffer() {
        return byteBuf;
    }

    @Override
    public VirtualBuffer getReadBuffer() {
        throw new UnsupportedOperationException();
    }

    /**
     * 为确保消息尽可能发送，UDP不支持立即close
     *
     * @param immediate true:立即关闭,false:响应消息发送完后关闭
     */
    @Override
    public void close(boolean immediate) {
//        byteBuf.flush();
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public void setId(String id) {
        throw new  UnsupportedOperationException("UDP content unsuppert set id");
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
        throw new UnsupportedOperationException();
    }
}
