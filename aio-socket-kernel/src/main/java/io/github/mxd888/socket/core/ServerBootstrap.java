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

import io.github.mxd888.socket.plugins.Plugin;
import io.github.mxd888.socket.utils.pool.memory.MemoryPoolFactory;
import io.github.mxd888.socket.utils.pool.memory.MemoryPool;
import io.github.mxd888.socket.utils.pool.memory.MemoryUnitFactory;
import io.github.mxd888.socket.intf.AioHandler;
import io.github.mxd888.socket.plugins.AioPlugins;
import io.github.mxd888.socket.utils.AIOUtil;
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
import java.util.concurrent.ExecutorService;
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
     * 内核IO线程池线程数量
     */
    private int bossThreadNum;

    /**
     * 作业任务线程池线程数量
     */
    private int workerThreadNum;

    /**
     * 内存池
     */
    private MemoryPool memoryPool;

    /**
     * 内核IO线程池
     */
    private ExecutorService bossExecutorService;

    /**
     * 作业池线程（aio-socket定制线程池）
     */
    private ExecutorService workerExecutorService;

    /**
     * 读完成处理类
     */
    private ReadCompletionHandler aioReadCompletionHandler;

    /**
     * 写完成处理类
     */
    private WriteCompletionHandler aioWriteCompletionHandler;

    /**
     * AIO 通道组
     */
    private AsynchronousChannelGroup asynchronousChannelGroup;

    /**
     * AIO Server 通道
     */
    private AsynchronousServerSocketChannel serverSocketChannel;

    /**
     * 服务器配置类
     */
    private final AioConfig config = new AioConfig(true);

    /**
     * socketChannel 和 ChannelContext联系
     */
    private Function<AsynchronousSocketChannel, TCPChannelContext> aioChannelContextFunction;

    /**
     * 虚拟内存工厂，这里为读操作获取虚拟内存
     */
    private final MemoryUnitFactory readMemoryUnitFactory = memoryBlock -> memoryBlock.allocate(getConfig().getReadBufferSize());

    /**
     * ServerBootstrap
     * @param host    内网地址
     * @param port    端口
     * @param handler 任务处理器
     */
    public ServerBootstrap(String host, int port, AioHandler handler) {
        this.config.setHost(host);
        this.config.setPort(port);
        this.config.getPlugins().setAioHandler(handler);
    }

    /**
     * 启动AIO Socket Server
     */
    public void start() {
        startExecutorService();
        start0(channel -> new TCPChannelContext(channel, getConfig(), this.aioReadCompletionHandler,
                this.aioWriteCompletionHandler, this.memoryPool.allocateBufferPage(), this.workerExecutorService));
    }

    /**
     * 内部启动逻辑
     *
     * @param aioContextFunction 通道和上下文信息的联系
     */
    private void start0(Function<AsynchronousSocketChannel, TCPChannelContext> aioContextFunction) {
        try {
            checkAndResetConfig();
            this.aioChannelContextFunction = aioContextFunction;
            this.aioReadCompletionHandler = new ReadCompletionHandler();
            this.aioWriteCompletionHandler = new WriteCompletionHandler();
            if (this.memoryPool == null) {
                this.memoryPool = getConfig().getMemoryPoolFactory().create();
            }
            AsynchronousChannelProvider provider = AsynchronousChannelProvider.provider();
            this.asynchronousChannelGroup = provider.openAsynchronousChannelGroup(this.bossExecutorService, 0);
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
                context.initTCPChannelContext(this.readMemoryUnitFactory.createBuffer(this.memoryPool.allocateBufferPage()));
            } else {
                AIOUtil.close(channel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (context == null) {
                AIOUtil.close(channel);
            } else {
                context.close(true);
            }
        }
    }

    /**
     * 检查配置项
     */
    private void checkAndResetConfig() {
        AioPlugins plugins = getConfig().getPlugins();
        getConfig().setMonitor(plugins)
                .setHandler(plugins);
        if (getConfig().getMaxOnlineNum() == 0) {
            // 默认单台aio-socket内核承载1000人，根据具体情况可以自由设置
            getConfig().setMaxOnlineNum(1000);
        }
    }

    /**
     * 启动内核线程池
     */
    private void startExecutorService() {
        if (this.bossThreadNum > 0) {
            this.bossExecutorService = ThreadUtils.getGroupExecutor(this.bossThreadNum);
        }else {
            this.bossExecutorService = ThreadUtils.getGroupExecutor();
        }
        if (this.workerThreadNum > 0) {
            this.workerExecutorService = ThreadUtils.getAioExecutor(this.workerThreadNum);
        }else {
            this.workerExecutorService = ThreadUtils.getAioExecutor();
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
            if (!this.workerExecutorService.isTerminated()) {
                this.workerExecutorService.shutdown();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (this.memoryPool != null) {
            this.memoryPool.release();
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
     * 获取作业线程池，通常使用了aio-socket后便不用在建立新的线程池，
     * 直接使用aio-socket的作业线程池即可
     *
     * @return 作业线程池
     */
    public ExecutorService getWorkerExecutorService() {
        return this.workerExecutorService;
    }

    /**
     * 设置线程池线程数量
     *
     * @param bossThreadNum   内核IO线程数量
     * @param workerThreadNum 普通作业线程数量
     * @return                this
     */
    public ServerBootstrap setThreadNum(int bossThreadNum, int workerThreadNum) {
        this.bossThreadNum = bossThreadNum;
        this.workerThreadNum = workerThreadNum;
        return this;
    }

    /**
     * 设置内存池工厂
     *
     * @param memoryPoolFactory 内存池工厂
     * @return this
     */
    public ServerBootstrap setMemoryPoolFactory(MemoryPoolFactory memoryPoolFactory) {
        getConfig().setMemoryPoolFactory(memoryPoolFactory);
        return this;
    }

    /**
     * 设置写缓冲区大小
     *
     * @param writeBufferSize 写缓冲区大小
     * @param maxWaitNum      最大等待队列长度
     * @return                this
     */
    public ServerBootstrap setWriteBufferSize(int writeBufferSize, int maxWaitNum) {
        getConfig().setWriteBufferSize(writeBufferSize)
                .setMaxOnlineNum(maxWaitNum);
        return this;
    }

    /**
     * 设置读缓冲区大小
     *
     * @param readBufferSize 读缓冲区大小
     * @return               this
     */
    public ServerBootstrap setReadBufferSize(int readBufferSize) {
        getConfig().setReadBufferSize(readBufferSize);
        return this;
    }

    /**
     * 注册插件
     *
     * @param plugin 插件项
     * @return       this
     */
    public ServerBootstrap addPlugin(Plugin plugin) {
        getConfig().getPlugins().addPlugin(plugin);
        return this;
    }

}
