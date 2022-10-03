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

import io.github.mxd888.socket.utils.pool.buffer.BufferPage;
import io.github.mxd888.socket.utils.pool.buffer.VirtualBuffer;
import io.github.mxd888.socket.core.AioConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public final class UDPChannel {

    private static final Logger LOGGER = LoggerFactory.getLogger(UDPChannel.class);

    private final BufferPage bufferPage;

    /**
     * 待输出消息
     */
    private ConcurrentLinkedQueue<ResponseUnit> responseTasks;

    private final Semaphore writeSemaphore = new Semaphore(1);

    private Worker worker;

    final AioConfig config;

    /**
     * 真实的UDP通道
     */
    private final DatagramChannel channel;

    private SelectionKey selectionKey;

    //发送失败的
    private ResponseUnit failResponseUnit;

    UDPChannel(final DatagramChannel channel, AioConfig config, BufferPage bufferPage) {
        this.channel = channel;
        this.bufferPage = bufferPage;
        this.config = config;
    }

    UDPChannel(final DatagramChannel channel, Worker worker, AioConfig config, BufferPage bufferPage) {
        this(channel, config, bufferPage);
        responseTasks = new ConcurrentLinkedQueue<>();
        this.worker = worker;
        worker.addRegister(selector -> {
            try {
                UDPChannel.this.selectionKey = channel.register(selector, SelectionKey.OP_READ, UDPChannel.this);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
        });
    }

    void write(VirtualBuffer virtualBuffer, UDPChannelContext session) {
        if (writeSemaphore.tryAcquire() && responseTasks.isEmpty() && send(virtualBuffer.buffer(), session) > 0) {
            virtualBuffer.clean();
            writeSemaphore.release();
            session.getWriteBuffer().flush();
            return;
        }
        responseTasks.offer(new ResponseUnit(session, virtualBuffer));
        if (selectionKey == null) {
            worker.addRegister(selector -> selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE));
        } else {
            if ((selectionKey.interestOps() & SelectionKey.OP_WRITE) == 0) {
                selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);
            }
        }
    }

    void doWrite() {
        while (true) {
            ResponseUnit responseUnit;
            if (failResponseUnit == null) {
                responseUnit = responseTasks.poll();
            } else {
                responseUnit = failResponseUnit;
                failResponseUnit = null;
            }
            if (responseUnit == null) {
                writeSemaphore.release();
                if (responseTasks.isEmpty()) {
                    selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
                    if (!responseTasks.isEmpty()) {
                        selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);
                    }
                }
                return;
            }
            if (send(responseUnit.response.buffer(), responseUnit.session) > 0) {
                responseUnit.response.clean();
                responseUnit.session.getWriteBuffer().flush();
            } else {
                failResponseUnit = responseUnit;
                LOGGER.warn("send fail,will retry...");
                break;
            }
        }
    }

    private int send(ByteBuffer byteBuffer, UDPChannelContext session) {
        if (config.getMonitor() != null) {
            config.getMonitor().beforeWrite(session);
        }
        int size;
        try {
            size = channel.send(byteBuffer, session.getRemoteAddress());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (config.getMonitor() != null) {
            config.getMonitor().afterWrite(session, size);
        }
        return size;
    }

    public UDPChannelContext connect(SocketAddress remote) {
        return new UDPChannelContext(this, remote, bufferPage);
    }

    public UDPChannelContext connect(String host, int port) {
        return connect(new InetSocketAddress(host, port));
    }

    public void close() {
        LOGGER.info("close channel...");
        if (selectionKey != null) {
            Selector selector = selectionKey.selector();
            selectionKey.cancel();
            selector.wakeup();
            selectionKey = null;
        }
        try {
            if (channel != null) {
                channel.close();
            }
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        //内存回收
        ResponseUnit task;
        while ((task = responseTasks.poll()) != null) {
            task.response.clean();
        }
        if (failResponseUnit != null) {
            failResponseUnit.response.clean();
        }
//        if (worker != null) {
//            worker.shutdown();
//        }
    }

    BufferPage getBufferPage() {
        return bufferPage;
    }


    DatagramChannel getChannel() {
        return channel;
    }

    static final class ResponseUnit {
        /**
         * 待输出数据的接受地址
         */
        private final UDPChannelContext session;
        /**
         * 待输出数据
         */
        private final VirtualBuffer response;

        public ResponseUnit(UDPChannelContext session, VirtualBuffer response) {
            this.session = session;
            this.response = response;
        }

    }
}
