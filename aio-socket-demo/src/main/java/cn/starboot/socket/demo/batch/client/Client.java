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
package cn.starboot.socket.demo.batch.client;

import cn.starboot.socket.Packet;
import cn.starboot.socket.codec.bytes.BytesPacket;
import cn.starboot.socket.core.Aio;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.ClientBootstrap;
import cn.starboot.socket.jdk.aio.ImproveAsynchronousChannelGroup;
import cn.starboot.socket.utils.ThreadUtils;

import java.io.IOException;

public class Client {

	public static void main(String[] args) throws IOException {

		String data = "hello aio-socket";
		Packet bytesPacket = new BytesPacket(data.getBytes());
		ImproveAsynchronousChannelGroup asynchronousChannelGroup =
				ImproveAsynchronousChannelGroup
						.withCachedThreadPool(ThreadUtils.getGroupExecutor(Runtime.getRuntime().availableProcessors()),
								Runtime.getRuntime().availableProcessors());
		ClientHandler clientHandler = new ClientHandler();
		for (int i = 0; i < 10; i++) {
			new Thread(() -> {
				ClientBootstrap bootstrap = new ClientBootstrap("localhost", 8888, clientHandler);
				try {
					ChannelContext channelContext =
							bootstrap.setBufferFactory(1024 * 1024 * 4, 1, true)
									.setReadBufferSize(1024 * 1024)
									.setWriteBufferSize(1024 * 1024, 512)
									.setMemoryKeep(true)
									.start(asynchronousChannelGroup);

					//(测试1和测试2的注释，不要同时打开，因为每个都是while(true).都打开没有测试的意义)
					// 测试1. 多包发送，适合用作流媒体服务器开发
					Aio.multiSend(channelContext,
							outputChannelContext -> {
								while (true) {
									outputChannelContext.write(bytesPacket);
								}
							});

					// 测试2. 单包发送，适合用作HTTP、websocket、IM、IOT和RPC等单包场景
//					while (true) {
//						Aio.send(channelContext, bytesPacket);
//					}


					// 关闭服务器
//                    bootstrap.shutdown();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}).start();
		}
	}

}
