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
package cn.starboot.socket.demo.batch.stream;

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
 * ------------------------------------------------
 * 			aio-socket performance
 * -------------------in 5 sec --------------------
 * inflow:				1422.926399230957(MB)
 * outflow:			    1421.8663444519043(MB)
 * process fail:		0
 * process count:		16207917
 * process total:		141181524
 * read count:			1423
 * write count:		    364174
 * connect count:		0
 * disconnect count:	0
 * online count:		10
 * connected total:	    10
 * Requests/sec:		3241583.4
 * Transfer/sec:		284.5852798461914(MB)
 * ------------------------------------------------
 */
public class Client {

    public static void main(String[] args) throws IOException {

        Packet demoPacket = new StringPacket("hello aio-socket");
		ImproveAsynchronousChannelGroup asynchronousChannelGroup = ImproveAsynchronousChannelGroup.withCachedThreadPool(ThreadUtils.getGroupExecutor(2), 2);
        MemoryPoolFactory poolFactory = () -> new MemoryPool(32 * 1024 * 1024, 10, true);
        ClientHandler clientHandler = new ClientHandler();
        // 5000
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                // 127.0.0.1
                ClientBootstrap bootstrap = new ClientBootstrap("localhost", 8888, clientHandler);
                bootstrap.setBufferFactory(1024 * 1024, 1, true)
                        .setReadBufferSize(1024 * 1024)
						.setThreadNum(2)
                        .setWriteBufferSize(1024 * 1024, 512)
                ;
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
