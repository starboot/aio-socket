package io.github.mxd888.socket.core;

import io.github.mxd888.socket.utils.pool.buffer.VirtualBuffer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * 抽象通道上下文
 */
public abstract class ChannelContext {

    /**
     * ChannelContext状态:已关闭
     */
    protected static final byte CHANNEL_STATUS_CLOSED = 1;

    /**
     * ChannelContext状态:关闭中
     */
    protected static final byte CHANNEL_STATUS_CLOSING = 2;

    /**
     * ChannelContext状态:正常
     */
    protected static final byte CHANNEL_STATUS_ENABLED = 3;

    /**
     * 当前会话状态
     */
    protected byte status = CHANNEL_STATUS_ENABLED;

    /**
     * 附件对象
     */
    private Object attachment;

    private final Map<String, Object> attr = new HashMap<>();


    public abstract VirtualBuffer getVirtualBuffer();

    public abstract WriteBuffer getWriteBuffer();

    public abstract VirtualBuffer getReadBuffer();

    public abstract void close(boolean immediate);

    /**
     * 获取通道ID
     *
     * @return 通道上下文唯一ID
     */
    public abstract String getId();

    /**
     * 通道上下文绑定唯一ID
     *
     * @param id ID内容
     */
    public abstract void setId(String id);

    public abstract void signalRead();

    /**
     * 获取当前会话的本地连接地址
     *
     * @return 本地地址
     * @throws IOException IO异常
     * @see AsynchronousSocketChannel#getLocalAddress()
     */
    public abstract InetSocketAddress getLocalAddress() throws IOException;

    /**
     * 获取当前会话的远程连接地址
     *
     * @return 远程地址
     * @throws IOException IO异常
     * @see AsynchronousSocketChannel#getRemoteAddress()
     */
    public abstract InetSocketAddress getRemoteAddress() throws IOException;

    /**
     * 当前会话是否已失效
     *
     * @return 是否失效
     */
    public boolean isInvalid() {
        return status != CHANNEL_STATUS_ENABLED;
    }

    /**
     * 获取附件对象
     *
     * @return 附件
     */
    public Object getAttachment() {
        return attachment;
    }

    /**
     * 存放附件，支持任意类型
     *
     * @param attachment 附件对象
     */
    public void setAttachment(Object attachment) {
        this.attachment = attachment;
    }

    public Map<String, Object> getAttr() {
        return attr;
    }

    public abstract AioConfig getAioConfig();
}
