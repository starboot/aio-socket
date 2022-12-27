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
package cn.starboot.socket.udp;

import cn.starboot.socket.intf.AioHandler;
import cn.starboot.socket.plugins.Plugin;
import cn.starboot.socket.utils.pool.memory.MemoryPoolFactory;
import cn.starboot.socket.utils.pool.memory.MemoryPool;
import cn.starboot.socket.core.AioConfig;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

public class UDPBootstrap {

    private MemoryPool bufferPool;

    private MemoryPool innerBufferPool;

    private final AioConfig config = new AioConfig(true);

    private Worker worker;

    private boolean innerWorker = false;

    public UDPBootstrap(AioHandler handler) {
        config.getPlugins().addAioHandler(handler);
        config.setHandler(config.getPlugins());
        this.worker = worker;
    }

    /**
     * 开启一个UDP通道，端口号随机
     *
     * @return UDP通道
     * @throws IOException 开启异常
     */
    public UDPChannel open() throws IOException {
        return open(0);
    }

    /**
     * 开启一个UDP通道
     *
     * @param port 指定绑定端口号,为0则随机指定
     * @return     UDPChannel通道
     * @throws IOException 开启异常
     */
    public UDPChannel open(int port) throws IOException {
        return open(null, port);
    }

    /**
     * 开启一个UDP通道
     *
     * @param host 绑定本机地址
     * @param port 指定绑定端口号,为0则随机指定
     * @return      UDPChannel通道
     * @throws IOException 开启异常
     */
    public UDPChannel open(String host, int port) throws IOException {
        if (bufferPool == null) {
            this.bufferPool = config.getMemoryPoolFactory().create();
            this.innerBufferPool = bufferPool;
        }
        if (worker == null) {
            innerWorker = true;
            worker = new Worker(bufferPool, 5);
        }
        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        if (port > 0) {
            InetSocketAddress inetSocketAddress = host == null ? new InetSocketAddress(port) : new InetSocketAddress(host, port);
            channel.socket().bind(inetSocketAddress);
        }
        return new UDPChannel(channel, worker, config, bufferPool.allocateBufferPage());
    }

    /**
     * 关闭UDP服务
     */
    public void shutdown() {
        if (innerWorker) {
            worker.shutdown();
        }
        if (innerBufferPool != null) {
            innerBufferPool.release();
        }
    }

    /**
     * 设置读缓存区大小
     * @return  this
     * @param size 单位：byte
     *
     */
    public final UDPBootstrap setReadBufferSize(int size) {
        this.config.setReadBufferSize(size);
        return this;
    }

    /**
     * 设置内存池的构造工厂。
     * 通过工厂形式生成的内存池会强绑定到当前UdpBootstrap对象，
     * 在UDPBootstrap执行shutdown时会释放内存池。
     * <b>在启用内存池的情况下会有更好的性能表现</b>
     *
     * @param memoryPoolFactory 内存池工厂
     * @return              当前AioQuickServer对象
     */
    public final UDPBootstrap setBufferFactory(MemoryPoolFactory memoryPoolFactory) {
        this.config.setMemoryPoolFactory(memoryPoolFactory);
        this.bufferPool = null;
        return this;
    }

    /**
     * 注册插件
     *
     * @param plugin 插件项
     * @return       this
     */
    public final UDPBootstrap addPlugin(Plugin plugin) {
        this.config.getPlugins().addPlugin(plugin);
        return this;
    }
}
