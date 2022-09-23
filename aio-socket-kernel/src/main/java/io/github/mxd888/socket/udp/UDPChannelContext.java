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
import io.github.mxd888.socket.buffer.BufferPage;
import io.github.mxd888.socket.buffer.VirtualBuffer;
import io.github.mxd888.socket.core.WriteBuffer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

final class UDPChannelContext {

    private final UDPChannel udpChannel;

    private final SocketAddress remote;

//    private final WriteBuffer byteBuf;

    UDPChannelContext(final UDPChannel udpChannel, final SocketAddress remote, BufferPage bufferPage) {
        this.udpChannel = udpChannel;
        this.remote = remote;
        Consumer<WriteBuffer> consumer = var -> {
//            VirtualBuffer writeBuffer = var.poll();
//            if (writeBuffer != null) {
//                udpChannel.write(writeBuffer, UDPChannelContext.this);
//            }
        };
//        this.byteBuf = new WriteBuffer(bufferPage, consumer, udpChannel.config.getWriteBufferSize(), 1);
//        udpChannel.config.getHandler().stateEvent(this, StateMachineEnum.NEW_CHANNEL, null);
    }

//    public WriteBuffer writeBuffer() {
//        return byteBuf;
//    }

    public ByteBuffer readBuffer() {
        throw new UnsupportedOperationException();
    }

    public void awaitRead() {
        throw new UnsupportedOperationException();
    }

    public void signalRead() {
        throw new UnsupportedOperationException();
    }

    /**
     * 为确保消息尽可能发送，UDP不支持立即close
     *
     * @param 、 true:立即关闭,false:响应消息发送完后关闭
     */
//    public void close(boolean immediate) {
//        byteBuf.flush();
//    }

    public InetSocketAddress getLocalAddress() throws IOException {
        return (InetSocketAddress) udpChannel.getChannel().getLocalAddress();
    }

    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) remote;
    }
}
