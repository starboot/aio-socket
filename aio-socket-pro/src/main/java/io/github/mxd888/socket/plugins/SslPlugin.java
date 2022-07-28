package io.github.mxd888.socket.plugins;

import io.github.mxd888.socket.buffer.BufferFactory;
import io.github.mxd888.socket.buffer.BufferPagePool;
import io.github.mxd888.socket.core.AioConfig;
import io.github.mxd888.socket.plugins.ssl.ClientAuth;
import io.github.mxd888.socket.plugins.ssl.SslAsynchronousSocketChannel;
import io.github.mxd888.socket.plugins.ssl.SslService;
import io.github.mxd888.socket.plugins.ssl.factory.ClientSSLContextFactory;
import io.github.mxd888.socket.plugins.ssl.factory.SSLContextFactory;
import io.github.mxd888.socket.plugins.ssl.factory.ServerSSLContextFactory;

import javax.net.ssl.SSLEngine;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.function.Consumer;

/**
 * SSL/TLS通信插件
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public final class SslPlugin extends AbstractPlugin {
    private final SslService sslService;
    private final BufferPagePool bufferPagePool;

    public SslPlugin(SSLContextFactory factory, Consumer<SSLEngine> consumer) throws Exception {
        this(factory, consumer, BufferFactory.ENABLE_BUFFER_FACTORY.create());
    }

    public SslPlugin(SSLContextFactory factory, Consumer<SSLEngine> consumer, BufferPagePool bufferPagePool) throws Exception {
        this.bufferPagePool = bufferPagePool;
        sslService = new SslService(factory.create(), consumer);
        System.out.println("aio-socket "+"version: " + AioConfig.VERSION + "; server kernel's stream SSL/TLS plugin added successfully");
    }

    public SslPlugin(ClientSSLContextFactory factory) throws Exception {
        this(factory, BufferFactory.ENABLE_BUFFER_FACTORY.create());
    }

    public SslPlugin(ClientSSLContextFactory factory, BufferPagePool bufferPagePool) throws Exception {
        this(factory, sslEngine -> sslEngine.setUseClientMode(true), bufferPagePool);
    }

    public SslPlugin(ServerSSLContextFactory factory, ClientAuth clientAuth) throws Exception {
        this(factory, clientAuth, BufferFactory.ENABLE_BUFFER_FACTORY.create());
    }

    public SslPlugin(ServerSSLContextFactory factory, ClientAuth clientAuth, BufferPagePool bufferPagePool) throws Exception {
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
        }, bufferPagePool);
    }

    @Override
    public AsynchronousSocketChannel shouldAccept(AsynchronousSocketChannel channel) {
        return new SslAsynchronousSocketChannel(channel, sslService, bufferPagePool.allocateBufferPage());
    }

}
