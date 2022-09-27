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
import io.github.mxd888.socket.utils.pool.buffer.BufferPagePool;
import io.github.mxd888.socket.core.Aio;
import io.github.mxd888.socket.core.TCPChannelContext;
import io.github.mxd888.socket.core.ClientBootstrap;
import io.github.mxd888.socket.plugins.ReconnectPlugin;

import java.io.IOException;
import java.io.PrintStream;


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
                // 127.0.0.1
                ClientBootstrap clientBootstrap = new ClientBootstrap((args != null && args.length != 0) ? args[0] : "127.0.0.1", (args != null && args.length != 0) ? Integer.parseInt(args[1]) : 8888, new ClientHandler());
                clientBootstrap.getConfig()
                        .setHeartPacket(new DemoPacket("heart message"))
                        .setReadBufferSize(1024 * 1024)
                        .setWriteBufferSize(1024 * 1024)
                        .setWriteBufferCapacity(16)
                        .setBufferFactory(() -> new BufferPagePool(50 * 1024 * 1024, 2, false))
                        .setEnhanceCore(true)
                        // 启用插件
                        .setEnablePlugins(true)
                        .getPlugins()
                        .addPlugin(new ReconnectPlugin(clientBootstrap));

                try {
                    TCPChannelContext start = clientBootstrap.start();
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
