/*
 *    Copyright 2019 The aio-socket Project
 *
 *    The aio-socket Project Licenses this file to you under the Apache License,
 *    Version 2.0 (the "License"); you may not use this file except in compliance
 *    with the License. You may obtain a copy of the License at:
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package io.github.mxd888.demo.client;

import io.github.mxd888.demo.common.DemoPacket;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.plugins.ACKPlugin;
import io.github.mxd888.socket.utils.pool.memory.MemoryPool;
import io.github.mxd888.socket.core.Aio;
import io.github.mxd888.socket.core.ClientBootstrap;
import io.github.mxd888.socket.plugins.ReconnectPlugin;

import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

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
//        demoPacket.setReq("177");   设置同步位
        // 5000
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                // 127.0.0.1
                ClientBootstrap bootstrap = new ClientBootstrap("localhost", 8888, new ClientHandler());
                bootstrap.setBufferFactory(() -> new MemoryPool(32 * 1024 * 1024, 10, true))
                        .setReadBufferSize(1024 * 1024)
                        .setWriteBufferSize(1024 * 1024, 512)
                        .addHeartPacket(new DemoPacket("heartbeat message"))
//                        .addPlugin(new MonitorPlugin(5))
                        .addPlugin(new ACKPlugin(5, TimeUnit.SECONDS, (packet, lastTime) -> System.out.println(packet.getReq() + " 超时了")))
                        .addPlugin(new ReconnectPlugin(bootstrap));

                try {
                    ChannelContext start = bootstrap.start();
                    long num = 0;
                    long startTime = System.currentTimeMillis();
                    while (num++ < Integer.MAX_VALUE) {
                        Aio.send(start, demoPacket);
                    }
                    System.out.println("安全消息结束" + (System.currentTimeMillis() - startTime));
                    Thread.sleep(10000);
                    bootstrap.shutdown();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

            }).start();
        }
    }

    private static boolean filterLog(String x){
        return x.contains("aio-socket version: 2.10.1.v20211002-RELEASE;");
    }

}
