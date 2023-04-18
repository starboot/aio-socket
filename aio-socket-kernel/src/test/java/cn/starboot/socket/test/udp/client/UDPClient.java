package cn.starboot.socket.test.udp.client;

import cn.starboot.socket.core.Aio;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.test.udp.UDPPacket;
import cn.starboot.socket.udp.UDPBootstrap;
import cn.starboot.socket.udp.UDPChannel;

import java.util.concurrent.CountDownLatch;

public class UDPClient {

    public static void main(String[] args) throws InterruptedException {

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
                    Thread.sleep(1000);
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
