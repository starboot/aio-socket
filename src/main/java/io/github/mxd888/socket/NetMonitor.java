package io.github.mxd888.socket;

import io.github.mxd888.socket.core.ChannelContext;

import java.nio.channels.AsynchronousSocketChannel;

/**
 * 网络监控器
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public interface NetMonitor {

    /**
     * 监控已接收到的连接
     *
     * @param channel                    当前已经建立连接的通道对象
     * @return AsynchronousSocketChannel 非null:接受该连接,null:拒绝该连接
     */
    AsynchronousSocketChannel shouldAccept(AsynchronousSocketChannel channel);

    /**
     * 监控触发本次读回调ChannelContext的已读数据字节数
     *
     * @param context  当前执行read的AioSession对象
     * @param readSize 已读数据长度
     */
    void afterRead(ChannelContext context, int readSize);

    /**
     * 即将开始读取数据
     *
     * @param context 当前会话对象
     */
    void beforeRead(ChannelContext context);

    /**
     * 监控触发本次写回调session的已写数据字节数
     *
     * @param context   本次执行write回调的AIOSession对象
     * @param writeSize 本次输出的数据长度
     */
    void afterWrite(ChannelContext context, int writeSize);

    /**
     * 即将开始写数据
     *
     * @param context 当前会话对象
     */
    void beforeWrite(ChannelContext context);
}
