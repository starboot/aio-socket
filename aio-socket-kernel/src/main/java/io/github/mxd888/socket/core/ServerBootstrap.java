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

import io.github.mxd888.socket.enhance.EnhanceAsynchronousChannelProvider;
import io.github.mxd888.socket.utils.pool.buffer.BufferPagePool;
import io.github.mxd888.socket.utils.pool.buffer.VirtualBufferFactory;
import io.github.mxd888.socket.intf.AioHandler;
import io.github.mxd888.socket.plugins.AioPlugins;
import io.github.mxd888.socket.utils.IOUtil;
import io.github.mxd888.socket.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * AIO Server 启动类
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class ServerBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerBootstrap.class);

    /**
     * 服务器配置类
     */
    private final AioConfig config = new AioConfig(true);

    /**
     * 内存池
     */
    private BufferPagePool bufferPool;

    /**
     * 读完成处理类
     */
    private ReadCompletionHandler aioReadCompletionHandler;

    /**
     * 写完成处理类
     */
    private WriteCompletionHandler aioWriteCompletionHandler;

    /**
     * socketChannel 和 ChannelContext联系
     */
    private Function<AsynchronousSocketChannel, TCPChannelContext> aioChannelContextFunction;

    /**
     * AIO Server 通道
     */
    private AsynchronousServerSocketChannel serverSocketChannel;

    /**
     * AIO 通道组
     */
    private AsynchronousChannelGroup asynchronousChannelGroup;

    /**
     * 虚拟内存工厂，这里为读操作获取虚拟内存
     */
    private final VirtualBufferFactory readBufferFactory = bufferPage -> bufferPage.allocate(getConfig().getReadBufferSize());

    public ServerBootstrap(int port, AioHandler handler) {
        this.config.setPort(port);
        this.config.setHandler(handler);
    }

    public ServerBootstrap(String host, int port, AioHandler handler) {
        this(port, handler);
        this.config.setHost(host);
    }

    /**
     * 启动AIO Socket Server
     */
    public void start() {
        start0(channel -> new TCPChannelContext(channel, getConfig(), this.aioReadCompletionHandler,
                this.aioWriteCompletionHandler, this.bufferPool.allocateBufferPage()));
    }

    /**
     * 内部启动逻辑
     *
     * @param aioContextFunction 通道和上下文信息的联系
     */
    private void start0(Function<AsynchronousSocketChannel, TCPChannelContext> aioContextFunction) {
        try {
            checkAndResetConfig();
            this.aioWriteCompletionHandler = new WriteCompletionHandler();
            if (this.bufferPool == null) {
                this.bufferPool = getConfig().getBufferFactory().create();
            }
            this.aioChannelContextFunction = aioContextFunction;
            AsynchronousChannelProvider provider;
            if (getConfig().isEnhanceCore()) {
                // 增强版
                provider = EnhanceAsynchronousChannelProvider.provider();
            } else {
                // 普通版
                provider = AsynchronousChannelProvider.provider();
            }
            this.aioReadCompletionHandler = new ReadCompletionHandler();
            this.asynchronousChannelGroup = provider.openAsynchronousChannelGroup(ThreadUtils.getGroupExecutor(), 0);
            this.serverSocketChannel = provider.openAsynchronousServerSocketChannel(this.asynchronousChannelGroup);
            if (getConfig().getSocketOptions() != null) {
                for (Map.Entry<SocketOption<Object>, Object> entry : getConfig().getSocketOptions().entrySet()) {
                    this.serverSocketChannel.setOption(entry.getKey(), entry.getValue());
                }
            }
            if (getConfig().getHost() != null) {
                this.serverSocketChannel.bind(new InetSocketAddress(getConfig().getHost(), getConfig().getPort()), getConfig().getBacklog());
            } else {
                this.serverSocketChannel.bind(new InetSocketAddress(getConfig().getPort()), getConfig().getBacklog());
            }
            startAcceptThread();
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("aio-socket version: {}; server kernel started successfully", AioConfig.VERSION);
            }
        }catch (IOException e) {
            shutdown();
            LOGGER.error("aio-socket version: {}; server kernel started failed", AioConfig.VERSION);
        }
    }

    /**
     * 启动接受连接请求的监听
     */
    private void startAcceptThread() {
        this.serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            @Override
            public void completed(AsynchronousSocketChannel channel, Void attachment) {
                try {
                    serverSocketChannel.accept(attachment, this);
                } catch (Throwable throwable) {
                    failed(throwable, attachment);
                    serverSocketChannel.accept(attachment, this);
                } finally {
                    initChannelContext(channel);
                }
            }
            @Override
            public void failed(Throwable exc, Void attachment) {
                exc.printStackTrace();
            }
        });
    }

    /**
     * 初始化每个链接通道
     *
     * @param channel 用户通道
     */
    private void initChannelContext(AsynchronousSocketChannel channel) {
        //连接成功则构造ChannelContext对象
        TCPChannelContext context = null;
        AsynchronousSocketChannel acceptChannel = channel;
        try {
            if (this.config.getMonitor() != null) {
                acceptChannel = getConfig().getMonitor().shouldAccept(channel);
            }
            if (acceptChannel != null) {
                acceptChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
                context = this.aioChannelContextFunction.apply(acceptChannel);
                context.initTCPChannelContext(this.readBufferFactory.createBuffer(this.bufferPool.allocateBufferPage()));
            } else {
                IOUtil.close(channel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (context == null) {
                IOUtil.close(channel);
            } else {
                context.close(true);
            }
        }
    }

    /**
     * 检查配置项
     */
    private void checkAndResetConfig() {
        // 检查是否启用插件模块
        if (getConfig().isEnablePlugins()) {
            AioPlugins plugins = getConfig().getPlugins();
            plugins.setAioHandler(getConfig().getHandler());
            getConfig().setMonitor(plugins)
                    .setHandler(plugins);
        }
        if (getConfig().getMaxOnlineNum() == 0) {
            // 默认单台aio-socket内核承载1000人，根据具体情况可以自由设置
            getConfig().setMaxOnlineNum(1000);
        }
    }

    /**
     * 停止服务端
     */
    public void shutdown() {
        try {
            if (this.serverSocketChannel != null) {
                this.serverSocketChannel.close();
                this.serverSocketChannel = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!this.asynchronousChannelGroup.isTerminated()) {
            try {
                this.asynchronousChannelGroup.shutdownNow();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            this.asynchronousChannelGroup.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (this.bufferPool != null) {
            this.bufferPool.release();
        }
    }

    /**
     * 获取配置信息
     *
     * @return AioConfig
     */
    public AioConfig getConfig() {
        return this.config;
    }

}
