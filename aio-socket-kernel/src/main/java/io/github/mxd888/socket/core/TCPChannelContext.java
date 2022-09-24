package io.github.mxd888.socket.core;

import io.github.mxd888.socket.NetMonitor;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.StateMachineEnum;
import io.github.mxd888.socket.utils.pool.buffer.BufferPage;
import io.github.mxd888.socket.utils.pool.buffer.VirtualBuffer;
import io.github.mxd888.socket.intf.AioHandler;
import io.github.mxd888.socket.utils.IOUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 通道上下文信息类
 * socket发起连接和读写相关方法（全是异步操作） https://blog.csdn.net/weixin_45754452/article/details/121925936
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public final class TCPChannelContext extends ChannelContext{

    /**
     * 用户绑定ID
     */
    private String id;

    /**
     * 底层通信channel对象
     */
    private final AsynchronousSocketChannel channel;

    /**
     * 输出流，用于往输出buffer里面输入数据的对象
     */
    private final WriteBuffer byteBuf;

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
    private VirtualBuffer readBuffer;

    /**
     * 存放待发送的完整比特流
     */
    private VirtualBuffer writeBuffer;





    /**
     * 构造通道上下文对象
     *
     * @param channel                Socket通道
     * @param config                 配置项
     * @param readCompletionHandler  读回调
     * @param writeCompletionHandler 写回调
     * @param bufferPage             绑定内存页
     */
    TCPChannelContext(AsynchronousSocketChannel channel, final AioConfig config, ReadCompletionHandler readCompletionHandler, WriteCompletionHandler writeCompletionHandler, BufferPage bufferPage) {
        this.channel = channel;
        this.readCompletionHandler = readCompletionHandler;
        this.writeCompletionHandler = writeCompletionHandler;
        this.aioConfig = config;

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
        byteBuf = new WriteBuffer(bufferPage, flushConsumer, getAioConfig().getWriteBufferSize(), getAioConfig().getWriteBufferCapacity());
        //触发状态机
        getAioConfig().getHandler().stateEvent(this, StateMachineEnum.NEW_CHANNEL, null);
    }

    /**
     * 初始化Aio ChannelContext
     */
    void initSession(VirtualBuffer readBuffer) {
        this.readBuffer = readBuffer;
        this.readBuffer.buffer().flip();
        signalRead();
    }

    /**
     * 触发通道的读回调操作
     */
    void signalRead() {
        if (status == CHANNEL_STATUS_CLOSED) {
            return;
        }
        final ByteBuffer readBuffer = this.readBuffer.buffer();
        final AioHandler handler = getAioConfig().getHandler();
        while (readBuffer.hasRemaining() && status == CHANNEL_STATUS_ENABLED) {
            Packet dataEntry;
            try {
                dataEntry = handler.decode(this.readBuffer, this);
            } catch (Exception e) {
                handler.stateEvent(this, StateMachineEnum.DECODE_EXCEPTION, e);
                throw e;
            }
            if (dataEntry == null) {
                break;
            }

            //处理消息
            try {
                Packet packet = handler.handle(this, dataEntry);
                if (packet != null) {
                    Aio.send(this, packet);
                }
            } catch (Exception e) {
                handler.stateEvent(this, StateMachineEnum.PROCESS_EXCEPTION, e);
            }
        }
        if (eof || status == CHANNEL_STATUS_CLOSING) {
            close(false);
            handler.stateEvent(this, StateMachineEnum.INPUT_SHUTDOWN, null);
            return;
        }
        if (status == CHANNEL_STATUS_CLOSED) {
            return;
        }
        /*
         *
         * 相同点：调用完compact和clear方法之后的buffer对象一般都是继续往该buffer中写入数据的
         * 不同点：
         * （1）clear是把position=0，limit=capacity等，也就是说，除了内部数组，其他属性都还原到buffer创建时的初始值，而内部数组的数据虽然没赋为null，
         *     但只要不在clear之后误用buffer.get就不会有问题，正确用法是使用buffer.put从头开始写入数据;
         * （2）而capacity是把buffer中内部数组剩余未读取的数据复制到该数组从索引为0开始，然后position设置为复制剩余数据后的最后一位元素的索引+1，
         *     limit设置为capacity，此时在0~position之间是未读数据，而position~limit之间是buffer的剩余空间，可以put数据。
         *
         * 使用场景：
         * 当buffer被读取过，但想继续复用buffer时，可以执行compact把剩余未读取数据往缓冲数据前面移动，compact移动完后，可以再次使用put往该buffer里put数据，此时数据会被写到剩余数据之后。
         */
        readBuffer.compact();
        //读缓冲区已满
        if (!readBuffer.hasRemaining()) {
            RuntimeException exception = new RuntimeException("readBuffer overflow");
            handler.stateEvent(this, StateMachineEnum.DECODE_EXCEPTION, exception);
            throw exception;
        }
        //再次读
        continueRead(this.readBuffer);
    }

    /**
     * 触发通道读方法
     *
     * @param readBuffer 存放读出的数据buffer
     */
    private void continueRead(VirtualBuffer readBuffer) {
        NetMonitor monitor = getAioConfig().getMonitor();
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
    private void continueWrite(VirtualBuffer writeBuffer) {
        NetMonitor monitor = getAioConfig().getMonitor();
        if (monitor != null) {
            monitor.beforeWrite(this);
        }
        channel.write(writeBuffer.buffer(), 0L, TimeUnit.MILLISECONDS, this, writeCompletionHandler);
    }

    /**
     * 强制关闭当前AIOSession。
     * 若此时还存留待输出的数据，则会导致该部分数据丢失
     */
    public final void close() {
        close(false);
    }

    /**
     * 是否立即关闭会话
     *
     * @param immediate true:立即关闭,false:响应消息发送完后关闭
     */
    @Override
    public synchronized void close(boolean immediate) {
        if (status == CHANNEL_STATUS_CLOSED) {
            return;
        }
        status = immediate ? CHANNEL_STATUS_CLOSED : CHANNEL_STATUS_CLOSING;
        if (immediate) {
            try {
                byteBuf.close();
                readBuffer.clean();
                if (writeBuffer != null) {
                    writeBuffer.clean();
                    writeBuffer = null;
                }
            } finally {
                IOUtil.close(channel);
                getAioConfig().getHandler().stateEvent(this, StateMachineEnum.CHANNEL_CLOSED, null);
            }
        } else if ((writeBuffer == null || !writeBuffer.buffer().hasRemaining()) && byteBuf.isEmpty()) {
            close(true);
        } else {
            getAioConfig().getHandler().stateEvent(this, StateMachineEnum.CHANNEL_CLOSING, null);
            byteBuf.flush();
        }
    }

    void flipRead(boolean eof) {
        this.eof = eof;
        this.readBuffer.buffer().flip();
    }

    /**
     * 获取地址
     *
     * @return 本地地址
     * @throws IOException IO异常
     * @see AsynchronousSocketChannel#getLocalAddress()
     */
    @Override
    public final InetSocketAddress getLocalAddress() throws IOException {
        assertChannel();
        return (InetSocketAddress) channel.getLocalAddress();
    }

    /**
     * 获取远程地址
     *
     * @return 远程地址
     * @throws IOException IO异常
     * @see AsynchronousSocketChannel#getRemoteAddress()
     */
    @Override
    public final InetSocketAddress getRemoteAddress() throws IOException {
        assertChannel();
        return (InetSocketAddress) channel.getRemoteAddress();
    }

    /**
     * 断言当前会话是否可用
     *
     * @throws IOException IO异常
     */
    private void assertChannel() throws IOException {
        if (status == CHANNEL_STATUS_CLOSED || channel == null) {
            throw new IOException("session is closed");
        }
    }

    /**
     * 获取配置
     *
     * @return ServerConfig
     */
    public AioConfig getAioConfig() {
        return this.aioConfig;
    }

    /**
     * 获取通道ID
     *
     * @return 通道上下文唯一ID
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * 通道上下文绑定唯一ID
     *
     * @param id ID内容
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取一个虚拟内存用来存放待发送数据
     *
     * @return 在内存页中快速匹配一个虚拟内存
     */
    @Override
    public VirtualBuffer getVirtualBuffer() {
        return byteBuf.newVirtualBuffer();
    }

    @Override
    public WriteBuffer getWriteBuffer() {
        return byteBuf;
    }

    @Override
    public VirtualBuffer getReadBuffer() {
        return readBuffer;
    }

    /**
     * 获取附件对象
     *
     * @return 附件
     */
    @Override
    public Object getAttachment() {
        return super.getAttachment();
    }

    /**
     * 存放附件，支持任意类型
     *
     * @param attachment 附件对象
     */
    @Override
    public void setAttachment(Object attachment) {
        super.setAttachment(attachment);
    }

    @Override
    public Map<String, Object> getAttr() {
        return super.getAttr();
    }
}
