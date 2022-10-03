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

import io.github.mxd888.socket.Monitor;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.StateMachineEnum;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.exception.AioDecoderException;
import io.github.mxd888.socket.utils.pool.buffer.BufferPagePool;
import io.github.mxd888.socket.utils.pool.buffer.VirtualBuffer;
import io.github.mxd888.socket.core.AioConfig;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Consumer;

public final class Worker implements Runnable {
    private final static int MAX_READ_TIMES = 16;
    private static final Runnable SELECTOR_CHANNEL = () -> {
    };
    private static final Runnable SHUTDOWN_CHANNEL = () -> {
    };
    /**
     * 当前Worker绑定的Selector
     */
    private final Selector selector;
    /**
     * 内存池
     */
    private final BufferPagePool bufferPool;
    private final BlockingQueue<Runnable> requestQueue = new ArrayBlockingQueue<>(256);

    /**
     * 待注册的事件
     */
    private final ConcurrentLinkedQueue<Consumer<Selector>> registers = new ConcurrentLinkedQueue<>();

    private VirtualBuffer standbyBuffer;
    private final ExecutorService executorService;

    public Worker(BufferPagePool bufferPool, int threadNum) throws IOException {
        this.bufferPool = bufferPool;
        this.selector = Selector.open();
        try {
            this.requestQueue.put(SELECTOR_CHANNEL);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //启动worker线程组
        executorService = new ThreadPoolExecutor(threadNum, threadNum,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new ThreadFactory() {
            int i = 0;

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "aio-socket:udp-" + Worker.this.hashCode() + "-" + (++i));
            }
        });
        for (int i = 0; i < threadNum; i++) {
            executorService.execute(this);
        }
    }

    /**
     * 注册事件
     */
    void addRegister(Consumer<Selector> register) {
        registers.offer(register);
        selector.wakeup();
    }

    @Override
    public void run() {
        try {
            while (true) {
                Runnable runnable = requestQueue.take();
                //服务终止
                if (runnable == SHUTDOWN_CHANNEL) {
                    requestQueue.put(SHUTDOWN_CHANNEL);
                    selector.wakeup();
                    break;
                } else if (runnable == SELECTOR_CHANNEL) {
                    try {
                        doSelector();
                    } finally {
                        requestQueue.put(SELECTOR_CHANNEL);
                    }
                } else {
                    runnable.run();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doSelector() throws IOException {
        Consumer<Selector> register;
        while ((register = registers.poll()) != null) {
            register.accept(selector);
        }
        Set<SelectionKey> keySet = selector.selectedKeys();
        if (keySet.isEmpty()) {
            selector.select();
        }
        Iterator<SelectionKey> keyIterator = keySet.iterator();
        // 执行本次已触发待处理的事件
        while (keyIterator.hasNext()) {
            SelectionKey key = keyIterator.next();
            UDPChannel udpChannel = (UDPChannel) key.attachment();
            if (!key.isValid()) {
                keyIterator.remove();
                udpChannel.close();
                continue;
            }
            if (key.isWritable()) {
                udpChannel.doWrite();
            }
            if (key.isReadable() && !doRead(udpChannel)) {
                break;
            }
            keyIterator.remove();
        }
    }

    private boolean doRead(UDPChannel channel) throws IOException {
        int count = MAX_READ_TIMES;
        AioConfig config = channel.config;
        while (count-- > 0) {
            if (standbyBuffer == null) {
                standbyBuffer = channel.getBufferPage().allocate(config.getReadBufferSize());
            }
            ByteBuffer buffer = standbyBuffer.buffer();
            SocketAddress remote = channel.getChannel().receive(buffer);
            if (remote == null) {
                buffer.clear();
                return true;
            }
            VirtualBuffer readyBuffer = standbyBuffer;
            standbyBuffer = channel.getBufferPage().allocate(config.getReadBufferSize());
            buffer.flip();
            Runnable runnable = () -> {
                //解码
                ChannelContext session = new UDPChannelContext(channel, remote, bufferPool.allocateBufferPage());
                try {
                    Monitor netMonitor = config.getMonitor();
                    if (netMonitor != null) {
                        netMonitor.beforeRead(session);
                        netMonitor.afterRead(session, buffer.remaining());
                    }
                    do {
                        Packet request = config.getHandler().decode(readyBuffer, session);
                        //理论上每个UDP包都是一个完整的消息
                        if (request == null) {
                            config.getHandler().stateEvent(session, StateMachineEnum.DECODE_EXCEPTION, new AioDecoderException("decode result is null, buffer size: " + buffer.remaining()));
                            break;
                        } else {
                            config.getHandler().handle(session, request);
                        }
                    } while (buffer.hasRemaining());
                } catch (Throwable e) {
                    e.printStackTrace();
//                    config.getHandler().stateEvent(session, StateMachineEnum.DECODE_EXCEPTION, e);
                } finally {
//                    session.writeBuffer().flush();
                    readyBuffer.clean();
                }
            };
            if (!requestQueue.offer(runnable)) {
                return false;
            }
        }
        return true;
    }


    void shutdown() {
        try {
            requestQueue.put(SHUTDOWN_CHANNEL);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        selector.wakeup();
        executorService.shutdown();
        try {
            selector.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
