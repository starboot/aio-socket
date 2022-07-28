
package io.github.mxd888.socket.plugins.ssl;

import io.github.mxd888.socket.buffer.BufferPage;
import io.github.mxd888.socket.buffer.VirtualBuffer;
import io.github.mxd888.socket.plugins.channels.AsynchronousSocketChannelProxy;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class SslAsynchronousSocketChannel extends AsynchronousSocketChannelProxy {

    private final VirtualBuffer netWriteBuffer;
    private final VirtualBuffer netReadBuffer;
    private final VirtualBuffer appReadBuffer;
    private SSLEngine sslEngine = null;
    /**
     * 完成握手置null
     */
    private HandshakeModel handshakeModel;
    /**
     * 完成握手置null
     */
    private final SslService sslService;

    private boolean handshake = true;
    /**
     * 自适应的输出长度
     */
    private int adaptiveWriteSize = -1;

    public SslAsynchronousSocketChannel(AsynchronousSocketChannel asynchronousSocketChannel, SslService sslService, BufferPage bufferPage) {
        super(asynchronousSocketChannel);
        this.handshakeModel = sslService.createSSLEngine(asynchronousSocketChannel, bufferPage);
        this.sslService = sslService;
        this.sslEngine = handshakeModel.getSslEngine();
        this.netWriteBuffer = handshakeModel.getNetWriteBuffer();
        this.netReadBuffer = handshakeModel.getNetReadBuffer();
        this.appReadBuffer = handshakeModel.getAppReadBuffer();
    }

    @Override
    public <A> void read(ByteBuffer dst, long timeout, TimeUnit unit, A attachment, CompletionHandler<Integer, ? super A> handler) {
        if (handshake) {
            handshakeModel.setHandshakeCallback(new HandshakeCallback() {
                @Override
                public void callback() {
                    handshake = false;
                    synchronized (SslAsynchronousSocketChannel.this) {
                        //释放内存
                        handshakeModel.getAppWriteBuffer().clean();
                        netReadBuffer.buffer().clear();
                        netWriteBuffer.buffer().clear();
                        appReadBuffer.buffer().clear().flip();
                        SslAsynchronousSocketChannel.this.notifyAll();
                    }
                    if (handshakeModel.isEof()) {
                        handler.completed(-1, attachment);
                    } else {
                        SslAsynchronousSocketChannel.this.read(dst, timeout, unit, attachment, handler);
                    }
                    handshakeModel = null;
                }
            });
            //触发握手
            sslService.doHandshake(handshakeModel);
            return;
        }
        ByteBuffer appBuffer = appReadBuffer.buffer();
        if (appBuffer.hasRemaining()) {
            int pos = dst.position();
            if (appBuffer.remaining() > dst.remaining()) {
                int limit = appBuffer.limit();
                appBuffer.limit(appBuffer.position() + dst.remaining());
                dst.put(appBuffer);
                appBuffer.limit(limit);
            } else {
                dst.put(appBuffer);
            }
            handler.completed(dst.position() - pos, attachment);
            return;
        }

        asynchronousSocketChannel.read(netReadBuffer.buffer(), timeout, unit, attachment, new CompletionHandler<Integer, A>() {
            @Override
            public void completed(Integer result, A attachment) {
                int pos = dst.position();
                ByteBuffer appBuffer = appReadBuffer.buffer();
                appBuffer.clear();
                doUnWrap();
                appBuffer.flip();
                if (appBuffer.remaining() > dst.remaining()) {
                    int limit = appBuffer.limit();
                    appBuffer.limit(appBuffer.position() + dst.remaining());
                    dst.put(appBuffer);
                    appBuffer.limit(limit);
                } else if (appBuffer.hasRemaining()) {
                    dst.put(appBuffer);
                } else if (result > 0) {
                    appBuffer.compact();
                    asynchronousSocketChannel.read(netReadBuffer.buffer(), timeout, unit, attachment, this);
                    return;
                }

                handler.completed(result != -1 ? dst.position() - pos : result, attachment);
            }

            @Override
            public void failed(Throwable exc, A attachment) {
                handler.failed(exc, attachment);
            }
        });
    }

    private void doUnWrap() {
        try {
            ByteBuffer netBuffer = netReadBuffer.buffer();
            ByteBuffer appBuffer = appReadBuffer.buffer();
            netBuffer.flip();
            SSLEngineResult result = sslEngine.unwrap(netBuffer, appBuffer);
            boolean closed = false;
            while (!closed && result.getStatus() != SSLEngineResult.Status.OK) {
                switch (result.getStatus()) {
                    case BUFFER_OVERFLOW:
                        System.out.println("BUFFER_OVERFLOW error");
                        break;
                    case BUFFER_UNDERFLOW:
                        if (netBuffer.limit() == netBuffer.capacity()) {
                            System.out.println("BUFFER_UNDERFLOW error");
                        } else {
                            if (netBuffer.position() > 0) {
                                netBuffer.compact();
                            } else {
                                netBuffer.position(netBuffer.limit());
                                netBuffer.limit(netBuffer.capacity());
                            }
                        }
                        return;
                    case CLOSED:
                        System.out.println("doUnWrap Result:" + result.getStatus());
                        closed = true;
                        break;
                    default:
                        System.out.println("doUnWrap Result:" + result.getStatus());
                }
                result = sslEngine.unwrap(netBuffer, appBuffer);
            }
            netBuffer.compact();
        } catch (SSLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Future<Integer> read(ByteBuffer dst) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <A> void read(ByteBuffer[] dsts, int offset, int length, long timeout, TimeUnit unit, A attachment, CompletionHandler<Long, ? super A> handler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <A> void write(ByteBuffer src, long timeout, TimeUnit unit, A attachment, CompletionHandler<Integer, ? super A> handler) {
        if (handshake) {
            checkInitialized();
        }
        int pos = src.position();
        doWrap(src);
        asynchronousSocketChannel.write(netWriteBuffer.buffer(), timeout, unit, attachment, new CompletionHandler<Integer, A>() {
            @Override
            public void completed(Integer result, A attachment) {
                if (result == -1) {
                    System.err.println("SslAsynchronousSocketChannel");
                }
                if (netWriteBuffer.buffer().hasRemaining()) {
                    asynchronousSocketChannel.write(netWriteBuffer.buffer(), timeout, unit, attachment, this);
                } else {
                    handler.completed(src.position() - pos, attachment);
                }
            }

            @Override
            public void failed(Throwable exc, A attachment) {
                handler.failed(exc, attachment);
            }
        });
    }

    /**
     * 校验是否已完成初始化,如果还处于Handshake阶段则阻塞当前线程
     */
    private void checkInitialized() {
        if (!handshake) {
            return;
        }
        synchronized (this) {
            if (!handshake) {
                return;
            }
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void doWrap(ByteBuffer writeBuffer) {
        try {
            ByteBuffer netBuffer = netWriteBuffer.buffer();
            netBuffer.compact();
            int limit = writeBuffer.limit();
            if (adaptiveWriteSize > 0 && writeBuffer.remaining() > adaptiveWriteSize) {
                writeBuffer.limit(writeBuffer.position() + adaptiveWriteSize);
            }
            SSLEngineResult result = sslEngine.wrap(writeBuffer, netBuffer);
            while (result.getStatus() != SSLEngineResult.Status.OK) {
                switch (result.getStatus()) {
                    case BUFFER_OVERFLOW:
                        netBuffer.clear();
                        writeBuffer.limit(writeBuffer.position() + ((writeBuffer.limit() - writeBuffer.position() >> 1)));
                        adaptiveWriteSize = writeBuffer.remaining();
//                        System.err.println("doWrap BUFFER_OVERFLOW maybeSize: " + maybeWriteSize);
                        break;
                    case BUFFER_UNDERFLOW:
                        System.err.println("doWrap BUFFER_UNDERFLOW");
                        break;
                    default:
                        System.err.println("doWrap Result:" + result.getStatus());
                }
                result = sslEngine.wrap(writeBuffer, netBuffer);
            }
            writeBuffer.limit(limit);
            netBuffer.flip();
        } catch (SSLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Future<Integer> write(ByteBuffer src) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <A> void write(ByteBuffer[] srcs, int offset, int length, long timeout, TimeUnit unit, A attachment, CompletionHandler<Long, ? super A> handler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws IOException {
        netWriteBuffer.clean();
        netReadBuffer.clean();
        appReadBuffer.clean();
        try {
            sslEngine.closeInbound();
        } catch (SSLException e) {
            System.err.println("ignore closeInbound exception: " + e.getMessage());
        }
        sslEngine.closeOutbound();
        asynchronousSocketChannel.close();

    }
}
