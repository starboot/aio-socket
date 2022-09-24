package io.github.mxd888.socket.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * AIO Client，可以从控制台输入报文，然后发送给 AIO Server
 */
public class AioClient implements Runnable {
    /**
     * AIO 客务端 Channel
     */
    private AsynchronousSocketChannel client;
    /**
     * 服务端地址
     */
    private InetSocketAddress remoteAddress;
    /**
     * 待发送数据的消息队列
     */
    private BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public AioClient(String host, int port) throws IOException {
        this.client = AsynchronousSocketChannel.open();
        this.remoteAddress = new InetSocketAddress(host, port);
    }

    public static void main(String[] args) throws IOException {
        AioClient client = new AioClient("127.0.0.1", 8989);
        new Thread(client).start();

        while (true) {
            // 模拟业务数据不断放入消息队列
            Scanner scanner = new Scanner(System.in);
            client.queue.add(scanner.nextLine());
        }
    }

    @Override
    public void run() {

        client.connect(remoteAddress, null, new CompletionHandler<Void, Object>() {

            @Override
            public void completed(Void result, Object attachment) {
                System.out.println("============== connect to server successfully. ================");

                try {

                    /* 当消息队列中有元素，立即取出发送给 AIO Server */
                    while (true) {
                        String msg = queue.take();
                        client.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
                    }

                } catch (Exception e) {
                    /* TODO 结合具体业务逻辑处理... */
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                /* TODO 结合具体业务逻辑处理... */
            }
        });

        final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        client.read(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {

            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                System.out.println(result);
                System.out.println("client read data: " + new String(byteBuffer.array()));

                /* 监听新的消息，递归调用 */
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                client.read(byteBuffer, byteBuffer, this);
            }

            @Override
            public void failed(Throwable exc, ByteBuffer buffer) {
                /* TODO 结合具体业务逻辑处理... */
            }
        });
    }
}
