package io.github.mxd888.demo.server.tcp.client;

import io.github.mxd888.demo.server.tcp.DemoPacket;
import io.github.mxd888.socket.core.Aio;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.core.ClientBootstrap;


import java.io.IOException;

public class Client {

    public static void main(String[] args) {

        DemoPacket demoPacket = new DemoPacket("hello aio-socket");

        for (int i = 0; i < 1; i++) {
            new Thread(() -> {
                ClientBootstrap clientBootstrap = new ClientBootstrap("127.0.0.1", 8888, new ClientHandler());
                try {
                    ChannelContext start = clientBootstrap.start();
                    long num = 0;
                    long startnum = System.currentTimeMillis();
                    while (num++ < Integer.MAX_VALUE) {
                        Aio.send(start, demoPacket);
                        Thread.sleep(2000);
                    }
                    System.out.println("安全消息结束" + (System.currentTimeMillis() - startnum));
                    clientBootstrap.shutdown();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

            }).start();
        }
    }
}
