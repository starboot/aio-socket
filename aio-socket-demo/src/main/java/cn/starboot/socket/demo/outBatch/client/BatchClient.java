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
package cn.starboot.socket.demo.outBatch.client;

import cn.starboot.socket.Packet;
import cn.starboot.socket.codec.bytes.BytesPacket;
import cn.starboot.socket.core.Aio;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.ClientBootstrap;
import cn.starboot.socket.jdk.aio.ImproveAsynchronousChannelGroup;
import cn.starboot.socket.utils.ThreadUtils;

import java.io.IOException;

public class BatchClient {

    public static void main(String[] args) throws IOException {

		String ip = args[0];
		int port = Integer.parseInt(args[1]);
		int threadNum = Integer.parseInt(args[2]);
		System.out.println("IP: " + ip + ", Port: " + port + ". ThreadNum: " + threadNum + " \r\n");

		String data = "hello aio-socket";
		Packet bytesPacket = new BytesPacket(data.getBytes());
		ImproveAsynchronousChannelGroup asynchronousChannelGroup = ImproveAsynchronousChannelGroup.withCachedThreadPool(ThreadUtils.getGroupExecutor(Runtime.getRuntime().availableProcessors()), Runtime.getRuntime().availableProcessors());
        ClientHandler clientHandler = new ClientHandler();
        for (int i = 0; i < threadNum; i++) {
            new Thread(() -> {
                // 127.0.0.1
                ClientBootstrap bootstrap = new ClientBootstrap(ip, port, clientHandler);
                bootstrap.setBufferFactory(1024 * 1024 * 4, 1, true)
                        .setReadBufferSize(1024 * 1024)
                        .setWriteBufferSize(1024 * 1024, 512)
                ;
                try {
                    ChannelContext start = bootstrap.start(asynchronousChannelGroup);
                    while (true) {
                        Aio.send(start, bytesPacket);
                    }
//                    bootstrap.shutdown();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }).start();
        }
    }

}
