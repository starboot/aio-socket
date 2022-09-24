package io.github.mxd888.socket.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * AIO 服务端，实现 不间断的 报文 收-处理-发 的过程
 */
public class AioServer implements Runnable {

    /**
     * 收发消息的编码格式
     */
    private final Charset utf8 = Charset.forName("UTF-8");
    /**
     * 服务端的处理逻辑(Function Interface)
     */
    private Function<String, String> service;
    /**
     * 服务端地址
     */
    private InetSocketAddress localAddress;
    /**
     * 异步 Channel 的分组管理器，它可以实现资源共享
     */
    private AsynchronousChannelGroup asynchronousChannelGroup = null;
    /**
     * AIO 服务端 Channel
     */
    private AsynchronousServerSocketChannel serverChannel = null;

    public AioServer(Function<String, String> service, int port) {
        this.service = service;
        this.localAddress = new InetSocketAddress(port);
    }

    public AioServer(Function<String, String> service, String hostname, int port) {
        this.service = service;
        this.localAddress = new InetSocketAddress(hostname, port);
    }

    @Override
    public void run() {

        try {

            init();

            serverChannel.accept(this, new CompletionHandler<AsynchronousSocketChannel, AioServer>() {

                @Override
                public void completed(AsynchronousSocketChannel socketChannel, AioServer aioServer) {
                    System.out.println("==============================================================");

                    System.out.println("server process begin...");

                    try {
                        System.out.println("client host: " + socketChannel.getRemoteAddress());

                        ByteBuffer buffer = ByteBuffer.allocate(1024);
//                        buffer.clear();
//                        buffer.flip();
                        socketChannel.read(buffer, 0L, TimeUnit.SECONDS, buffer, new ReadCompletionHandler(socketChannel));

                    } catch (Exception e) {
                        /* TODO 结合具体业务逻辑处理... */
                    } finally {
                        // 监听新的connect，递归调用
                        aioServer.serverChannel.accept(aioServer, this);
                    }
                }

                @Override
                public void failed(Throwable e, AioServer attachment) {
                    /* TODO 结合具体业务逻辑处理... */
                }

            });

        } catch (Exception e) {
            /* TODO 结合具体业务逻辑处理... */
        }

    }

    private class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

        final AsynchronousSocketChannel socketChannel;

        public ReadCompletionHandler(AsynchronousSocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }

        @Override
        public void completed(Integer result, ByteBuffer buffer) {
            buffer.flip();
            System.out.println("received : " + utf8.decode(buffer));

            buffer.rewind();
            /* TODO service 服务处理 收到的 报文，并加以处理 */
            String rspMsg = service.apply(utf8.decode(buffer).toString());

            socketChannel.write(ByteBuffer.wrap(rspMsg.getBytes(StandardCharsets.UTF_8)));

            ByteBuffer buffer_ = ByteBuffer.allocate(1024);
            socketChannel.read(buffer_, buffer_, this);
        }

        @Override
        public void failed(Throwable exc, ByteBuffer buffer) {
            /* TODO 结合具体业务逻辑处理... */
        }
    }

    private void init() throws IOException {

        this.serverChannel = AsynchronousServerSocketChannel.open(asynchronousChannelGroup);

        serverChannel.bind(localAddress);

        System.out.println("listening on : " + localAddress.getHostString());
    }

    // -----------------------------------------------------------
    // setter / getter
    // -----------------------------------------------------------

    public void setAsynchronousChannelGroup(AsynchronousChannelGroup asynchronousChannelGroup) {
        this.asynchronousChannelGroup = asynchronousChannelGroup;
    }

    public static void main(String[] args) throws IOException {
        /* AsynchronousChannelGroup 可以理解为一个 JVM 中对于Socket相关操作的一些公共资源的代表  */
        AsynchronousChannelGroup asynchronousChannelGroup = AsynchronousChannelGroup.withCachedThreadPool(Executors.newCachedThreadPool(), 10);

        AioServer aioServer = new AioServer( msg -> "Msg Server send : " + msg,"127.0.0.1", 8989);
        /* 系统中如果只有 一个 AioServer 服务端，此处可 set null，即不通过 AsynchronousChannelGroup 来新建 AsynchronousServerSocketChannel */
        aioServer.setAsynchronousChannelGroup(asynchronousChannelGroup);

        new Thread(aioServer).start();

        /* AIO 异步非阻塞，工作过程中不需要任何线程 监控，所以需要其他线程 保持工作状态，防止JVM关闭 */
        while (true) {

        }
    }
}
