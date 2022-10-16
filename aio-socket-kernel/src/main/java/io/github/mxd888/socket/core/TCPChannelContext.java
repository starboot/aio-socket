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
package io.github.mxd888.socket.core;

import io.github.mxd888.socket.Monitor;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.StateMachineEnum;
import io.github.mxd888.socket.exception.AioDecoderException;
import io.github.mxd888.socket.task.DecodeTask;
import io.github.mxd888.socket.task.HandlerTask;
import io.github.mxd888.socket.task.SendTask;
import io.github.mxd888.socket.utils.pool.memory.MemoryBlock;
import io.github.mxd888.socket.utils.pool.memory.MemoryUnit;
import io.github.mxd888.socket.intf.AioHandler;
import io.github.mxd888.socket.utils.AIOUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 通道上下文信息类
 * socket发起连接和读写相关方法（全是异步操作） https://blog.csdn.net/weixin_45754452/article/details/121925936
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public final class TCPChannelContext extends ChannelContext{

    /**
     * 底层通信channel对象
     */
    private final AsynchronousSocketChannel channel;

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
     * 存放刚读到的数据
     */
    private MemoryUnit readBuffer;

    /**
     * 存放待发送的完整比特流
     */
    private MemoryUnit writeBuffer;

    /**
     * 消息处理逻辑执行器
     */
    private HandlerTask handlerTask;

    /**
     * 消息发送逻辑执行器
     */
    private SendTask sendTask;

    /**
     * 消息解码逻辑执行器
     */
    private DecodeTask decodeTask;

    TCPChannelContext(AsynchronousSocketChannel channel,
                      final AioConfig config,
                      ReadCompletionHandler readCompletionHandler,
                      WriteCompletionHandler writeCompletionHandler,
                      MemoryBlock memoryBlock) {
        this(channel, config, readCompletionHandler, writeCompletionHandler, memoryBlock, null);
    }

    /**
     * 构造通道上下文对象
     *
     * @param channel                Socket通道
     * @param config                 配置项
     * @param readCompletionHandler  读回调
     * @param writeCompletionHandler 写回调
     * @param memoryBlock             绑定内存页
     */
    TCPChannelContext(AsynchronousSocketChannel channel,
                      final AioConfig config,
                      ReadCompletionHandler readCompletionHandler,
                      WriteCompletionHandler writeCompletionHandler,
                      MemoryBlock memoryBlock,
                      ExecutorService aioThreadPoolExecutor) {
        this.channel = channel;
        this.readCompletionHandler = readCompletionHandler;
        this.writeCompletionHandler = writeCompletionHandler;
        this.aioConfig = config;
        setAioExecutor(aioThreadPoolExecutor);

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

    /**
     * 初始化TCPChannelContext
     */
    void initTCPChannelContext(Supplier<MemoryUnit> supplier) {
        this.readBuffer = supplier.get();
        this.readBuffer.buffer().flip();
        signalRead(false);
    }

    /**
     * 触发通道的读回调操作
     */
    @Override
    public void signalRead(boolean isFlip) {
        flipRead(isFlip);
        if (status == CHANNEL_STATUS_CLOSED) {
            return;
        }
        final ByteBuffer readBuffer = this.readBuffer.buffer();
        final AioHandler handler = getAioConfig().getHandler();
        while (readBuffer.hasRemaining() && status == CHANNEL_STATUS_ENABLED) {
            Packet packet = null;
            try {
                if (getOldByteBuffer().isEmpty()) {
                    packet = handler.decode(this.readBuffer, this);
                }else {
                    getOldByteBuffer().offer(this.readBuffer);
                    packet = handler.decode(getOldByteBuffer().peek(), this);
                }
            } catch (AioDecoderException e) {
                handler.stateEvent(this, StateMachineEnum.DECODE_EXCEPTION, e);
                e.printStackTrace();
            }
            if (packet == null) {
                break;
            }
            aioHandler(packet);
        }
        if (eof || status == CHANNEL_STATUS_CLOSING) {
            close(false);
            handler.stateEvent(this, StateMachineEnum.INPUT_SHUTDOWN, null);
            return;
        }
        if (status == CHANNEL_STATUS_CLOSED) {
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
        }else {
            readBuffer.compact();
        }
        continueRead(this.readBuffer);
    }

    /**
     * 触发通道读方法
     *
     * @param readBuffer 存放读出的数据buffer
     */
    private void continueRead(MemoryUnit readBuffer) {
        Monitor monitor = getAioConfig().getMonitor();
        if (monitor != null) {
            monitor.beforeRead(this);
        }
        channel.read(readBuffer.buffer(), 0L, TimeUnit.MILLISECONDS, this, readCompletionHandler);
    }

    /**
     * 触发AIO的写操作,
     * 需要调用控制同步
     */
    void writeCompleted() {
        if (writeBuffer == null) {
            writeBuffer = byteBuf.pollItem();
        } else if (!writeBuffer.buffer().hasRemaining()) {
            writeBuffer.clean();
            writeBuffer = byteBuf.pollItem();
        }
        if (writeBuffer != null) {
            continueWrite(writeBuffer);
            return;
        }
        semaphore.release();
        //此时可能是Closing或Closed状态
        if (status != CHANNEL_STATUS_ENABLED) {
            close();
        } else {
            //也许此时有新的消息通过write方法添加到writeCacheQueue中
            byteBuf.flush();
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
        channel.write(writeBuffer.buffer(), 0L, TimeUnit.MILLISECONDS, this, writeCompletionHandler);
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
        if (status == CHANNEL_STATUS_CLOSED || channel == null) {
            throw new IOException("ChannelContext is closed");
        }
    }

    /**
     * 设置aio-socket线程池
     * @param aioThreadPoolExecutor 线程池
     */
    private void setAioExecutor(ExecutorService aioThreadPoolExecutor) {
        if (aioThreadPoolExecutor != null) {
            this.handlerTask = new HandlerTask(this, aioThreadPoolExecutor);
            this.sendTask = new SendTask(this, aioThreadPoolExecutor);
            this.decodeTask = new DecodeTask(this, aioThreadPoolExecutor);
        }
    }

    /**
     * 调用处理器
     * @param packet 消息包
     */
    private void aioHandler(Packet packet) {
        if (getAioConfig().isMultilevelModel() && handlerTask != null && handlerTask.addTask(packet)) {
            handlerTask.execute();
        }else {
            Packet handle = getAioConfig().getHandler().handle(this, packet);
            if (handle != null) {
                sendPacket(handle);
            }
        }
    }

    /**
     * 调用解码处理器
     * @param integer 读结果
     * @return        如果不存在解码处理器则返回false
     */
    protected boolean runDecodeRunnable(Integer integer) {
        if (this.decodeTask != null && this.decodeTask.addTask(integer)) {
            this.decodeTask.execute();
            return true;
        }else {
            return false;
        }
    }

    @Override
    public synchronized void close(boolean immediate) {
        if (status == CHANNEL_STATUS_CLOSED) {
            return;
        }
        status = immediate ? CHANNEL_STATUS_CLOSED : CHANNEL_STATUS_CLOSING;
        if (immediate) {
            try {
                this.byteBuf.close();
                readBuffer.clean();
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
            byteBuf.flush();
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
    public AioConfig getAioConfig() {
        return this.aioConfig;
    }

    @Override
    protected void sendPacket(Packet packet) {
        if (this.sendTask != null && this.sendTask.addTask(packet)) {
            this.sendTask.execute();
        }else {
            synchronized (this) {
                getAioConfig().getHandler().encode(packet, this);
            }
            getWriteBuffer().flush();
        }
    }

    @Override
    public MemoryUnit getReadBuffer() {
        return this.readBuffer;
    }

}
