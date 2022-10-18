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

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.codec.string.StringPacket;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.utils.ThreadUtils;
import io.github.mxd888.socket.utils.pool.memory.MemoryPool;
import io.github.mxd888.socket.core.Aio;
import io.github.mxd888.socket.core.ClientBootstrap;
import io.github.mxd888.socket.utils.pool.memory.MemoryPoolFactory;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.concurrent.ExecutorService;

/**
 * OUTPUT_EXCEPTION 异常
 * OUTPUT_EXCEPTION 异常
 * TCPChannelContext 已经关闭
 * INPUT_EXCEPTION 异常
 * java.io.IOException: writeBuffer has closed
 * 	at io.github.mxd888.socket.core.WriteBuffer.write(WriteBuffer.java:181)
 * 	at io.github.mxd888.socket.core.WriteBuffer.writeInt(WriteBuffer.java:137)
 * 	at io.github.mxd888.demo.common.Handler.encode(Handler.java:60)
 * 	at io.github.mxd888.socket.plugins.AioPlugins.encode(AioPlugins.java:113)
 * 	at io.github.mxd888.socket.core.TCPChannelContext.sendPacket(TCPChannelContext.java:349)
 * 	at io.github.mxd888.socket.core.Aio.send(Aio.java:63)
 * 	at io.github.mxd888.demo.client.Client.lambda$main$2(Client.java:75)
 * 	at java.lang.Thread.run(Thread.java:748)
 * Exception in thread "Thread-5" java.lang.RuntimeException: OutputStream has closed
 * 	at io.github.mxd888.socket.core.WriteBuffer.flush(WriteBuffer.java:253)
 * 	at io.github.mxd888.socket.core.TCPChannelContext.sendPacket(TCPChannelContext.java:350)
 * 	at io.github.mxd888.socket.core.Aio.send(Aio.java:63)
 * 	at io.github.mxd888.demo.client.Client.lambda$main$2(Client.java:75)
 * 	at java.lang.Thread.run(Thread.java:748)
 *
 * Process finished with exit code -1
 */
public class Client {

    public static void main(String[] args) throws IOException {

        Packet demoPacket = new StringPacket("hello aio-socket");
//        demoPacket.setReq("177");   设置同步位
        ExecutorService groupExecutor = ThreadUtils.getGroupExecutor(Runtime.getRuntime().availableProcessors());
        AsynchronousChannelGroup asynchronousChannelGroup = AsynchronousChannelGroup.withThreadPool(groupExecutor);
        MemoryPoolFactory poolFactory = () -> new MemoryPool(32 * 1024 * 1024, 10, true);
        // 5000
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                // 127.0.0.1
                ClientBootstrap bootstrap = new ClientBootstrap("localhost", 8888, new ClientHandler());
                bootstrap.setBufferFactory(poolFactory)
                        .setReadBufferSize(1024 * 1024)
                        .setWriteBufferSize(1024 * 1024, 512)
//                        .addHeartPacket(new DemoPacket("heartbeat message"))
//                        .addPlugin(new MonitorPlugin(5))
//                        .addPlugin(new ACKPlugin(5, TimeUnit.SECONDS, (packet, lastTime) -> System.out.println(packet.getReq() + " 超时了")))
//                        .addPlugin(new ReconnectPlugin(bootstrap))
                ;

                try {
                    ChannelContext start = bootstrap.start(asynchronousChannelGroup);
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

}
