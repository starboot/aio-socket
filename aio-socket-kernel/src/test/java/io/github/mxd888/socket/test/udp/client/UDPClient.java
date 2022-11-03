package io.github.mxd888.socket.test.udp.client;

import io.github.mxd888.socket.ProtocolEnum;
import io.github.mxd888.socket.core.Aio;
import io.github.mxd888.socket.core.ChannelContext;
//import io.github.mxd888.socket.plugins.MonitorPlugin;
import io.github.mxd888.socket.test.udp.UDPPacket;
import io.github.mxd888.socket.udp.UDPBootstrap;
import io.github.mxd888.socket.udp.UDPChannel;
import io.github.mxd888.socket.udp.Worker;
import io.github.mxd888.socket.utils.pool.memory.MemoryPool;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class UDPClient {

    public static void main(String[] args) throws IOException, InterruptedException {

        int c = 1;
        CountDownLatch count = new CountDownLatch(c);
        UDPPacket packet = new UDPPacket("hello aio-socket udp");
        ClientUDPHandler udpHandler = new ClientUDPHandler();
        for (int i = 0; i < c; i++) {
            new Thread(() -> {
                try {
                    UDPBootstrap bootstrap = new UDPBootstrap(udpHandler);
                    bootstrap
//                            .addPlugin(new MonitorPlugin(5))
                            .setReadBufferSize(1024);
                    UDPChannel channel = bootstrap.open();
                    ChannelContext session = channel.connect("localhost", 8888);
                    session.setProtocol(udpHandler.name());
                    for (int i1 = 0; i1 < 2; i1++) {
                        Aio.send(session, packet);
                        Thread.sleep(10);
                    }
                    count.countDown();
                    // 关闭会话
                    Thread.sleep(5000);
                    session.close();
//                    System.out.println("发送完毕");
                    bootstrap.shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }).start();
        }
        count.await();
        System.out.println("shutdown...");


    }

}
