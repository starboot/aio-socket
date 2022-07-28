package io.github.mxd888.socket.core;

import io.github.mxd888.socket.buffer.BufferPagePool;
import io.github.mxd888.socket.buffer.VirtualBufferFactory;
import io.github.mxd888.socket.intf.AioHandler;
import io.github.mxd888.socket.plugins.AioPlugins;
import io.github.mxd888.socket.utils.IOUtil;
import io.github.mxd888.socket.utils.ThreadUtils;

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
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * AIO Server 启动类
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class ServerBootstrap {

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
    private Function<AsynchronousSocketChannel, ChannelContext> aioChannelContextFunction;

    /**
     * AIO Server 通道
     */
    private AsynchronousServerSocketChannel serverSocketChannel;

    /**
     * AIO 通道组
     */
    private AsynchronousChannelGroup asynchronousChannelGroup;

    /**
     * 集群机器 IP+port
     */
    private String[] Cluster;

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
        start0(channel -> new ChannelContext(channel, getConfig(), this.aioReadCompletionHandler,
                this.aioWriteCompletionHandler, this.bufferPool.allocateBufferPage()));
    }

    /**
     * 内部启动逻辑
     *
     * @param aioContextFunction 通道和上下文信息的联系
     */
    private void start0(Function<AsynchronousSocketChannel, ChannelContext> aioContextFunction) {
        try {
            checkAndResetConfig();
            this.aioWriteCompletionHandler = new WriteCompletionHandler();
            if (this.bufferPool == null) {
                this.bufferPool = getConfig().getBufferFactory().create();
            }
            this.aioChannelContextFunction = aioContextFunction;
            AsynchronousChannelProvider provider = AsynchronousChannelProvider.provider();
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
            System.out.println("aio-socket "+"version: " + AioConfig.VERSION + "; server kernel started successfully");
        }catch (IOException e) {
            shutdown();
            System.out.println("aio-socket "+"version: " + AioConfig.VERSION + "; server kernel started failed");
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
        ChannelContext context = null;
        AsynchronousSocketChannel acceptChannel = channel;
        try {
            if (this.config.getMonitor() != null) {
                acceptChannel = getConfig().getMonitor().shouldAccept(channel);
            }
            if (acceptChannel != null) {
                acceptChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
                context = this.aioChannelContextFunction.apply(acceptChannel);
                context.initSession(this.readBufferFactory.createBuffer(this.bufferPool.allocateBufferPage()));
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
    private void checkAndResetConfig() throws IOException {
        // 检查是否启用插件模块
        if (getConfig().isEnablePlugins()) {
            AioPlugins plugins = getConfig().getPlugins();
            plugins.setAioHandler(getConfig().getHandler());
            getConfig().setMonitor(plugins);
            getConfig().setHandler(plugins);
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

    /**
     * 设置集群服务器IP、 port；当开启集群时才有效
     *
     * @param cluster 字符串数组类型
     */
    public void setCluster(String[] cluster) {
        this.Cluster = cluster;
    }
}
