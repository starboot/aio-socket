package io.github.mxd888.socket.test.udp.client;

import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.plugins.MonitorPlugin;
import io.github.mxd888.socket.udp.UDPBootstrap;
import io.github.mxd888.socket.udp.UDPChannel;
import io.github.mxd888.socket.udp.Worker;
import io.github.mxd888.socket.utils.pool.buffer.BufferPagePool;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class UDPClient {

    public static void main(String[] args) throws IOException, InterruptedException {

        BufferPagePool bufferPagePool = new BufferPagePool(1024 * 1024 * 16, Runtime.getRuntime().availableProcessors(), true);

        Worker worker = new Worker(bufferPagePool, Runtime.getRuntime().availableProcessors());
        int c = 5;
        CountDownLatch count = new CountDownLatch(c);
        byte[] bytes = "hello aio-socket".getBytes();
        for (int i = 0; i < c; i++) {
            new Thread(() -> {
                try {
                    UDPBootstrap bootstrap = new UDPBootstrap(new ClientUDPHandler(), worker);
                    bootstrap
                            .addPlugin(new MonitorPlugin(5))
                            .setBufferPagePool(bufferPagePool)
                            .setReadBufferSize(1024);
                    UDPChannel channel = bootstrap.open();
                    ChannelContext session = channel.connect("localhost", 8888);
                    for (int i1 = 0; i1 < 10; i1++) {
                        synchronized (session.getWriteBuffer()) {
                            session.getWriteBuffer().writeInt(bytes.length);
                            session.getWriteBuffer().write(bytes);
                            session.getWriteBuffer().flush();
                        }
                        Thread.sleep(10);
                    }
                    count.countDown();
                    // 关闭会话
                    session.close();
                    System.out.println("发送完毕");
//                    bootstrap.shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }).start();
        }
        count.await();
        System.out.println("shutdown...");

    }

}
