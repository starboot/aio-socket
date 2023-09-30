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
package cn.starboot.socket.demo.batch.requests;

import cn.starboot.socket.Packet;
import cn.starboot.socket.codec.string.StringPacket;
import cn.starboot.socket.core.Aio;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.ClientBootstrap;
import cn.starboot.socket.jdk.aio.ImproveAsynchronousChannelGroup;
import cn.starboot.socket.utils.ThreadUtils;
import cn.starboot.socket.utils.pool.memory.MemoryPool;
import cn.starboot.socket.utils.pool.memory.MemoryPoolFactory;

import java.io.IOException;
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
        ExecutorService groupExecutor = ThreadUtils.getGroupExecutor(Runtime.getRuntime().availableProcessors());
		ImproveAsynchronousChannelGroup asynchronousChannelGroup = ImproveAsynchronousChannelGroup.withThreadPool(groupExecutor);
        MemoryPoolFactory poolFactory = () -> new MemoryPool(32 * 1024 * 1024, 10, true);
        ClientHandler clientHandler = new ClientHandler();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                ClientBootstrap bootstrap = new ClientBootstrap("localhost", 8888, clientHandler);
                bootstrap.setBufferFactory(1024 * 1024, 1, true)
                        .setReadBufferSize(1024 * 1024)
                        .setWriteBufferSize(1024 * 1024, 512);
                try {
                    ChannelContext start = bootstrap.start(asynchronousChannelGroup);
                    while (true) {
                        Aio.send(start, demoPacket);
                    }
//                    bootstrap.shutdown();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }).start();
        }
    }

}
