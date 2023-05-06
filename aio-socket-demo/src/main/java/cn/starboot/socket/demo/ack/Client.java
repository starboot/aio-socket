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
package cn.starboot.socket.demo.ack;

import cn.starboot.socket.Packet;
import cn.starboot.socket.codec.string.StringPacket;
import cn.starboot.socket.core.Aio;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.ClientBootstrap;
import cn.starboot.socket.plugins.ACKPlugin;
import cn.starboot.socket.utils.pool.memory.MemoryPool;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Client {

    public static void main(String[] args) throws IOException {

        Packet demoPacket = new StringPacket("hello aio-socket");

        // 设置同部位
        demoPacket.setReq("test-ack");

		ClientBootstrap bootstrap = new ClientBootstrap("localhost", 8888, new ClientHandler());

		bootstrap.setBufferFactory(() -> new MemoryPool(2 * 1024 * 1024, 2, true))
				.setReadBufferSize(1024 * 1024)
				.setWriteBufferSize(1024 * 1024, 512)
				.addPlugin(new ACKPlugin(6, 2, TimeUnit.SECONDS, (packet, lastTime) -> System.out.println(packet.getReq() + " 超时了")));

		ChannelContext start = bootstrap.start();

		Aio.send(start, demoPacket);

    }

}
