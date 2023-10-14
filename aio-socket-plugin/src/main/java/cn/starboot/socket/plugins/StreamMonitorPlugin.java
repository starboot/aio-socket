/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.socket.plugins;


import cn.starboot.socket.plugins.channels.AsynchronousSocketChannelProxy;
import cn.starboot.socket.core.AioConfig;
import cn.starboot.socket.plugins.channels.UnsupportedAsynchronousSocketChannel;
import cn.starboot.socket.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * 传输层码流监控插件
 *
 * @author smart-socket: https://gitee.com/smartboot/smart-socket.git
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class StreamMonitorPlugin extends AbstractPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamMonitorPlugin.class);

    public static final BiConsumer<AsynchronousSocketChannel, byte[]> BLUE_HEX_INPUT_STREAM = (channel, bytes) -> {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(ConsoleColors.BLUE + simpleDateFormat.format(new Date())
                        + " [ " + channel.getRemoteAddress() + " --> "
                        + channel.getLocalAddress() + " ] [ read: "
                        + bytes.length + " bytes ]" + StringUtils.toHexString(bytes)
                        + ConsoleColors.RESET);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    };
    public static final BiConsumer<AsynchronousSocketChannel, byte[]> RED_HEX_OUTPUT_STREAM = (channel, bytes) -> {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(ConsoleColors.RED + simpleDateFormat.format(new Date())
                        + " [ " + channel.getLocalAddress() + " --> "
                        + channel.getRemoteAddress() + " ] [ write: "
                        + bytes.length + " bytes ]" + StringUtils.toHexString(bytes)
                        + ConsoleColors.RESET);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    public static final BiConsumer<AsynchronousSocketChannel, byte[]> BLUE_TEXT_INPUT_STREAM = (channel, bytes) -> {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(ConsoleColors.BLUE + simpleDateFormat.format(new Date())
                        + " [ " + channel.getRemoteAddress() + " --> "
                        + channel.getLocalAddress() + " ] [ read: "
                        + bytes.length + " bytes ]\r\n" + new String(bytes)
                        + ConsoleColors.RESET);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    };
    public static final BiConsumer<AsynchronousSocketChannel, byte[]> RED_TEXT_OUTPUT_STREAM = (channel, bytes) -> {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(ConsoleColors.RED + simpleDateFormat.format(new Date())
                        + " [ " + channel.getLocalAddress() + " --> "
                        + channel.getRemoteAddress() + " ] [ write: "
                        + bytes.length + " bytes ]\r\n" + new String(bytes)
                        + ConsoleColors.RESET);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    };
    private final BiConsumer<AsynchronousSocketChannel, byte[]> inputStreamConsumer;
    private final BiConsumer<AsynchronousSocketChannel, byte[]> outputStreamConsumer;

    public StreamMonitorPlugin() {
        this(BLUE_HEX_INPUT_STREAM, RED_HEX_OUTPUT_STREAM);

    }

    public StreamMonitorPlugin(BiConsumer<AsynchronousSocketChannel, byte[]> inputStreamConsumer, BiConsumer<AsynchronousSocketChannel, byte[]> outputStreamConsumer) {
        this.inputStreamConsumer = Objects.requireNonNull(inputStreamConsumer);
        this.outputStreamConsumer = Objects.requireNonNull(outputStreamConsumer);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("aio-socket version: " + AioConfig.VERSION + "; server kernel's stream monitor plugin added successfully");
        }
    }

//    @Override
    public AsynchronousSocketChannel shouldAccept(AsynchronousSocketChannel channel) {
        return new StreamMonitorAsynchronousSocketChannel(channel);
    }

    static class MonitorCompletionHandler<A> implements CompletionHandler<Integer, A> {
        CompletionHandler<Integer, A> handler;
        BiConsumer<AsynchronousSocketChannel, byte[]> consumer;
        ByteBuffer buffer;
        AsynchronousSocketChannel channel;

        public MonitorCompletionHandler(AsynchronousSocketChannel channel, CompletionHandler<Integer, A> handler, BiConsumer<AsynchronousSocketChannel, byte[]> consumer, ByteBuffer buffer) {
            this.channel = new UnsupportedAsynchronousSocketChannel(channel) {
                @Override
                public SocketAddress getRemoteAddress() throws IOException {
                    return channel.getRemoteAddress();
                }

                @Override
                public SocketAddress getLocalAddress() throws IOException {
                    return channel.getLocalAddress();
                }
            };
            this.handler = handler;
            this.consumer = consumer;
            this.buffer = buffer;
        }

        @Override
        public void completed(Integer result, A attachment) {
            if (result > 0) {
                byte[] bytes = new byte[result];
                buffer.position(buffer.position() - result);
                buffer.get(bytes);
                consumer.accept(channel, bytes);
            }
            handler.completed(result, attachment);
        }

        @Override
        public void failed(Throwable exc, A attachment) {
            handler.failed(exc, attachment);
        }
    }

    static class ConsoleColors {
        /**
         * 重置颜色
         */
        public static final String RESET = "\033[0m";
        /**
         * 蓝色
         */
        public static final String BLUE = "\033[34m";

        /**
         * 红色
         */
        public static final String RED = "\033[31m";

    }

    class StreamMonitorAsynchronousSocketChannel extends AsynchronousSocketChannelProxy {

        public StreamMonitorAsynchronousSocketChannel(AsynchronousSocketChannel asynchronousSocketChannel) {
            super(asynchronousSocketChannel);
        }

        @Override
        public <A> void read(ByteBuffer dst, long timeout, TimeUnit unit, A attachment, CompletionHandler<Integer, ? super A> handler) {
            super.read(dst, timeout, unit, attachment, new MonitorCompletionHandler<>(this, handler, inputStreamConsumer, dst));
        }

        @Override
        public <A> void write(ByteBuffer src, long timeout, TimeUnit unit, A attachment, CompletionHandler<Integer, ? super A> handler) {
            super.write(src, timeout, unit, attachment, new MonitorCompletionHandler<>(this, handler, outputStreamConsumer, src));
        }
    }
}
