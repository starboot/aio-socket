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
package cn.starboot.socket.plugins;

import cn.starboot.socket.plugins.ssl.ClientAuth;
import cn.starboot.socket.plugins.ssl.SslAsynchronousSocketChannel;
import cn.starboot.socket.plugins.ssl.SslService;
import cn.starboot.socket.utils.pool.memory.MemoryPool;
import cn.starboot.socket.utils.pool.memory.MemoryPoolFactory;
import cn.starboot.socket.core.AioConfig;
import cn.starboot.socket.plugins.ssl.factory.ClientSSLContextFactory;
import cn.starboot.socket.plugins.ssl.factory.SSLContextFactory;
import cn.starboot.socket.plugins.ssl.factory.ServerSSLContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLEngine;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.function.Consumer;

/**
 * SSL/TLS通信插件
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public final class SSLPlugin extends AbstractPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(SSLPlugin.class);

    private final SslService sslService;

    private final MemoryPool memoryPool;

    public SSLPlugin(SSLContextFactory factory, Consumer<SSLEngine> consumer) throws Exception {
        this(factory, consumer, MemoryPoolFactory.DISABLED_BUFFER_FACTORY.create());
    }

    public SSLPlugin(SSLContextFactory factory, Consumer<SSLEngine> consumer, MemoryPool memoryPool) throws Exception {
        this.memoryPool = memoryPool;
        sslService = new SslService(factory.create(), consumer);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("aio-socket "+"version: " + AioConfig.VERSION + "; server kernel's stream SSL/TLS plugin added successfully");
        }
    }

    public SSLPlugin(ClientSSLContextFactory factory) throws Exception {
        this(factory, MemoryPoolFactory.DISABLED_BUFFER_FACTORY.create());
    }

    public SSLPlugin(ClientSSLContextFactory factory, MemoryPool memoryPool) throws Exception {
        this(factory, sslEngine -> sslEngine.setUseClientMode(true), memoryPool);
    }

    public SSLPlugin(ServerSSLContextFactory factory, ClientAuth clientAuth) throws Exception {
        this(factory, clientAuth, MemoryPoolFactory.DISABLED_BUFFER_FACTORY.create());
    }

    public SSLPlugin(ServerSSLContextFactory factory, ClientAuth clientAuth, MemoryPool memoryPool) throws Exception {
        this(factory, sslEngine -> {
            sslEngine.setUseClientMode(false);
            switch (clientAuth) {
                case OPTIONAL:
                    sslEngine.setWantClientAuth(true);
                    break;
                case REQUIRE:
                    sslEngine.setNeedClientAuth(true);
                    break;
                case NONE:
                    break;
                default:
                    throw new Error("Unknown auth " + clientAuth);
            }
        }, memoryPool);
    }

    @Override
    public AsynchronousSocketChannel shouldAccept(AsynchronousSocketChannel channel) {
        return new SslAsynchronousSocketChannel(channel, sslService, memoryPool.allocateBufferPage());
    }

}
