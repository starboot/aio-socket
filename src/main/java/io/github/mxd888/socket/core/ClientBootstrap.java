package io.github.mxd888.socket.core;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.buffer.BufferPagePool;
import io.github.mxd888.socket.buffer.VirtualBufferFactory;
import io.github.mxd888.socket.cluster.ClusterBootstrap;
import io.github.mxd888.socket.intf.AioHandler;
import io.github.mxd888.socket.plugins.AioPlugins;
import io.github.mxd888.socket.plugins.ClusterPlugin;
import io.github.mxd888.socket.plugins.KernelProtocolPlugin;
import io.github.mxd888.socket.utils.IOUtil;
import io.github.mxd888.socket.utils.QuickTimerTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Map;
import java.util.Objects;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * AIO Client 启动类
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class ClientBootstrap {

    /**
     * 客户端服务配置。
     */
    private final AioConfig config = new AioConfig(false);

    /**
     * 网络连接的会话对象
     */
    private ChannelContext channelContext;

    /**
     * 内存池
     */
    private BufferPagePool bufferPool = null;

    /**
     * IO事件处理线程组。
     * 作为客户端，该AsynchronousChannelGroup只需保证2个长度的线程池大小即可满足通信读写所需。
     */
    private AsynchronousChannelGroup asynchronousChannelGroup;

    /**
     * 绑定本地地址
     */
    private SocketAddress localAddress;

    /**
     * 连接超时时间
     */
    private final static int connectTimeout = 2000;

    private boolean isCheck = true;

    /**
     * 构造虚拟缓冲区工厂
     */
    private final VirtualBufferFactory readBufferFactory = bufferPage -> bufferPage.allocate(this.config.getReadBufferSize());

    /**
     * 当前构造方法设置了启动Aio客户端的必要参数，基本实现开箱即用。
     *
     * @param host             远程服务器地址
     * @param port             远程服务器端口号
     * @param handler          协议编解码  消息处理器
     */
    public  ClientBootstrap(String host, int port, AioHandler handler) {
        this.config.setHost(host);
        this.config.setPort(port);
        this.config.setHandler(handler);
    }

    /**
     * 启动客户端。
     * 本方法会构建线程数为2的{@code asynchronousChannelGroup}
     * 并通过调用{@link ClientBootstrap#start(AsynchronousChannelGroup)}启动服务。
     *
     * @return                   建立连接后的会话对象
     * @throws IOException       IOException
     *
     * @see ClientBootstrap#start(AsynchronousChannelGroup)
     */
    public final ChannelContext start() throws IOException {
        this.asynchronousChannelGroup = AsynchronousChannelGroup.withFixedThreadPool(2, Thread::new);
        return start(this.asynchronousChannelGroup);
    }

    /**
     * 启动客户端。
     * 在与服务端建立连接期间，该方法处于阻塞状态。直至连接建立成功，或者发生异常。
     * 该start方法支持外部指定AsynchronousChannelGroup，实现多个客户端共享一组线程池资源，有效提升资源利用率。
     *
     * @param asynchronousChannelGroup IO事件处理线程组
     * @return                         建立连接后的会话对象
     * @throws IOException             IOException
     *
     * @see AsynchronousSocketChannel#connect(SocketAddress)
     */
    public ChannelContext start(AsynchronousChannelGroup asynchronousChannelGroup) throws IOException {
        if (isCheck) {
            checkAndResetConfig();
        }
        CompletableFuture<ChannelContext> future = new CompletableFuture<>();
        start(asynchronousChannelGroup, future, new CompletionHandler<ChannelContext, CompletableFuture<ChannelContext>>() {
            @Override
            public void completed(ChannelContext session, CompletableFuture<ChannelContext> future) {
                if (future.isDone() || future.isCancelled()) {
                    session.close();
                    System.out.println("aio-socket "+"version: " + AioConfig.VERSION + "; client kernel started failed because of future is done or cancelled");
                } else {
                    future.complete(session);
                    heartMessage();
                    System.out.println("aio-socket "+"version: " + AioConfig.VERSION + "; client kernel started successfully");
                }
            }

            @Override
            public void failed(Throwable exc, CompletableFuture<ChannelContext> future) {
                future.completeExceptionally(exc);
                System.out.println("aio-socket "+"version: " + AioConfig.VERSION + "; client kernel started failed");
            }
        });
        try {
            if (connectTimeout > 0) {
                return future.get(connectTimeout, TimeUnit.MILLISECONDS);
            } else {
                return future.get();
            }
        } catch (Exception e) {
            future.cancel(false);
            shutdownNow();
            throw new IOException(e);
        }
    }

    /**
     * 采用异步的方式启动客户端
     *
     * @param asynchronousChannelGroup  通信线程资源组
     * @param future                    可传入回调方法中的附件对象
     * @param handler                   异步回调
     * @throws IOException              .
     */
    private void start(AsynchronousChannelGroup asynchronousChannelGroup, CompletableFuture<ChannelContext> future,
                          CompletionHandler<ChannelContext, ? super CompletableFuture<ChannelContext>> handler) throws IOException {
        AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open(asynchronousChannelGroup);
        if (this.bufferPool == null) {
            this.bufferPool = getConfig().getBufferFactory().create();
        }
        if (this.config.getSocketOptions() != null) {
            for (Map.Entry<SocketOption<Object>, Object> entry : this.config.getSocketOptions().entrySet()) {
                socketChannel.setOption(entry.getKey(), entry.getValue());
            }
        }
        if (this.localAddress != null) {
            socketChannel.bind(this.localAddress);
        }
        socketChannel.connect(new InetSocketAddress(config.getHost(), config.getPort()), socketChannel, new CompletionHandler<Void, AsynchronousSocketChannel>() {
            @Override
            public void completed(Void result, AsynchronousSocketChannel socketChannel) {
                try {
                    AsynchronousSocketChannel connectedChannel = socketChannel;
                    if (config.getMonitor() != null) {
                        connectedChannel = config.getMonitor().shouldAccept(socketChannel);
                    }
                    if (connectedChannel == null) {
                        throw new RuntimeException("NetMonitor refuse channel");
                    }
                    //连接成功则构造AIOChannelContext对象
                    channelContext = new ChannelContext(connectedChannel, config, new ReadCompletionHandler(), new WriteCompletionHandler(), bufferPool.allocateBufferPage());
                    channelContext.initSession(readBufferFactory.createBuffer(bufferPool.allocateBufferPage()));
                    handler.completed(channelContext, future);
                } catch (Exception e) {
                    failed(e, socketChannel);
                }
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel socketChannel) {
                try {
                    handler.failed(exc, future);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (socketChannel != null) {
                        IOUtil.close(socketChannel);
                    }
                    shutdownNow();
                }
            }
        });
    }

    /**
     * 检查配置项
     */
    private void checkAndResetConfig() {
        // 检查是否启用插件模块
        if (getConfig().isEnablePlugins()) {
            AioPlugins plugins = getConfig().getPlugins();
            plugins.setAioHandler(getConfig().getHandler());
            getConfig().setMonitor(plugins);
            getConfig().setHandler(plugins);
            plugins.addPlugin(new KernelProtocolPlugin());
        }
        // 检查是否启用集群插件
        if (getConfig().isEnableCluster() && getConfig().isEnablePlugins()) {
            // 注册集群插件
            getConfig().getPlugins().addPlugin(new ClusterPlugin());
        }
    }

    /**
     * 心跳
     */
    private void heartMessage() {
        QuickTimerTask.SCHEDULED_EXECUTOR_SERVICE.schedule(()-> {
            System.out.println("aio-socket "+"version: " + AioConfig.VERSION + "; client kernel are sending heart");
            Packet packet = new Packet();
            packet.setFromId("15511090451");
            packet.setToId("15511090451");
            Aio.send(channelContext, packet);
            heartMessage();
        }, 20, TimeUnit.SECONDS);
    }

    /**
     * 获取连接上下文
     *
     * @return ChannelContext
     */
    public ChannelContext getChannelContext() {
        return this.channelContext;
    }

    /**
     * 停止客户端服务.
     * 调用该方法会触发AioSession的close方法
     * 并且如果当前客户端若是通过执行{@link ClientBootstrap#start()}方法构建的
     * 同时会触发asynchronousChannelGroup的shutdown动作。
     */
    public final void shutdown() {
        shutdown0(false);
    }

    /**
     * 立即关闭客户端
     */
    public final void shutdownNow() {
        shutdown0(true);
    }

    /**
     * 停止client
     *
     * @param flag 是否立即停止
     */
    private void shutdown0(boolean flag) {
        if (this.channelContext != null) {
            this.channelContext.close(flag);
            this.channelContext = null;
        }
        //仅Client内部创建的ChannelGroup需要shutdown
        if (this.asynchronousChannelGroup != null) {
            this.asynchronousChannelGroup.shutdown();
            this.asynchronousChannelGroup = null;
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

    public void setCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }
}
