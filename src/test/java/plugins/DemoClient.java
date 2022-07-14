package plugins;

import io.github.mxd888.socket.buffer.VirtualBuffer;
import io.github.mxd888.socket.core.Aio;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.core.ClientBootstrap;
import io.github.mxd888.socket.plugins.ReconnectPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DemoClient {

    private static boolean running = true;
    private static ChannelContext start;
    private static ClientBootstrap clientBootstrap;

    public static void main(String[] args) throws IOException {
        //
//        ClientBootstrap clientBootstrap = new ClientBootstrap("localhost", 8888, new DemoHandlerClient());
//        ChannelContext start = clientBootstrap.start();
//        WriteBuffer writeBuffer = start.writeBuffer();
//        byte[] data = "hello".getBytes();
//        writeBuffer.writeInt(data.length);
//        writeBuffer.write(data);
//        writeBuffer.flush();
//        ClientBootstrap clientBootstrap1 = new ClientBootstrap("localhost", 8888, new DemoHandler());
//        clientBootstrap1.start();
//        ClientBootstrap clientBootstrap2 = new ClientBootstrap("localhost", 8888, new DemoHandler());
//        clientBootstrap2.start();

//        MulitClient();
//        recoon();
//        ReconnPlug();

        Map<String, String> map = new HashMap<>();
        map.put("1","k");
        map.put("1","k2");
        map.put("1","k3");
        System.out.println(map.size()+map.get("1"));
    }

    public static void ReconnPlug() throws IOException {
        ClientBootstrap clientBootstrap = new ClientBootstrap("localhost", 8888, new DemoHandlerClient());
        clientBootstrap.getConfig().setEnablePlugins(true);
        clientBootstrap.getConfig().getPlugins().addPlugin(new ReconnectPlugin(clientBootstrap));
        ChannelContext start = clientBootstrap.start();
        Packet packet = new Packet();
        packet.setFromId("hello");
        Aio.send(start, packet);
    }

    static void recoon() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                System.out.println("启动连接监测");
                while (running) {
                    if (start == null || start.isInvalid()) {
                        System.out.println("连接异常，准备重连...");
                        connect();
                    } else {
                        System.out.println("连接正常...");
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("终止连接监测");
            }
        }, "Reconnect-Thread").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("模拟连接异常");
                int i = 0;
                while (i++ < 3) {
                    System.out.println("第 {"+i+"} 次断开连接");
                    if (start != null) {
                        start.close();
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("彻底断开连接，不再重连");
                shutdown();
            }
        }, "Fault-Thread").start();
    }

    static void MulitClient() {

        byte[] data = "hello".getBytes();

        for (int i = 0; i < 1; i++) {
            Runnable localhost = () -> {
                try {
                    ClientBootstrap clientBootstrap = new ClientBootstrap("localhost", 8888, new DemoHandlerClient());
                    ChannelContext start = clientBootstrap.start();
                    long num = 0;
                    long startnum = System.currentTimeMillis();
                    while (num++ < Integer.MAX_VALUE) {
                        Packet packet = new Packet();
                        packet.setFromId("hello");
                        Aio.send(start, packet);
                    }

                    System.out.println("安全消息结束" + (System.currentTimeMillis() - startnum));
                    clientBootstrap.shutdown();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            new Thread(localhost).start();
        }


    }

    public static void connect() {
        try {
            if (clientBootstrap != null) {
                System.out.println("关闭旧客户端");
                clientBootstrap.shutdownNow();
            }
            clientBootstrap = new ClientBootstrap("localhost", 8888, new DemoHandler());
            start = clientBootstrap.start();
            System.out.println("客户端连接成功");
        } catch (IOException e) {
            if (clientBootstrap != null) {
                clientBootstrap.shutdownNow();
            }
        }
    }

    public static void shutdown() {
        running = false;
        clientBootstrap.shutdownNow();
    }

}
