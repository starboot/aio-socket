package io.github.mxd888.demo.client;

import io.github.mxd888.demo.common.DemoPacket;
import io.github.mxd888.socket.buffer.BufferPagePool;
import io.github.mxd888.socket.core.Aio;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.core.ClientBootstrap;
import io.github.mxd888.socket.plugins.ReconnectPlugin;

import java.io.IOException;
import java.io.PrintStream;

/**
 * 内存池bug，多个线程同时往输出流里面写入数据，导致消息混乱
 */
public class Client {

    public static void main(String[] args) {

//        byte b = (byte) 0x7f;
//        System.out.println(b);

        PrintStream ps = new PrintStream(System.out){
            @Override
            public void println(String x) {
                if(filterLog(x)){
                    return;
                }
                super.println(x);
            }
            @Override
            public void print(String s) {
                if(filterLog(s)){
                    return;
                }
                super.print(s);
            }
        };
        System.setOut(ps);

        DemoPacket demoPacket = new DemoPacket("hello aio-socket");
        // 5000
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            new Thread(() -> {
                // 81.70.149.16 127.0.0.1
                ClientBootstrap clientBootstrap = new ClientBootstrap((args != null && args.length != 0) ? args[0] : "127.0.0.1", (args != null && args.length != 0) ? Integer.parseInt(args[1]) : 8888, new ClientHandler());
                clientBootstrap.getConfig()
                        .setHeartPacket(new DemoPacket("heart message"))
                        .setReadBufferSize(1024 * 1024)
                        .setWriteBufferSize(1024 * 1024)
                        .setWriteBufferCapacity(16)
                        .setBufferFactory(() -> new BufferPagePool(5 * 1024 * 1024, 9, true))
                        .setEnhanceCore(true)
                        // 启用插件
                        .setEnablePlugins(true)
                        .getPlugins()
                        .addPlugin(new ReconnectPlugin(clientBootstrap));

                try {
                    ChannelContext start = clientBootstrap.start();
                    long num = 0;
                    long startnum = System.currentTimeMillis();
                    while (num++ < Integer.MAX_VALUE) {
                        if (start == null) {
                            System.out.println("连接失败了.....");
                        }else {
//                            零拷贝优化前2000， 非零拷贝50
                            Aio.send(start, demoPacket);
                        }
                    }
                    System.out.println("安全消息结束" + (System.currentTimeMillis() - startnum));
                    Thread.sleep(10000);
                    clientBootstrap.shutdown();
                } catch (IOException | InterruptedException e) {
                    System.out.println(finalI);
                    e.printStackTrace();
                }

            }).start();
        }
    }

    private static boolean filterLog(String x){
        return x.contains("aio-socket version: 2.10.1.v20211002-RELEASE;");
    }

}
