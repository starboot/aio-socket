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
package cn.starboot.socket.demo.basic;

import cn.starboot.socket.Packet;
import cn.starboot.socket.codec.string.StringPacket;
import cn.starboot.socket.core.Aio;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.ClientBootstrap;
import cn.starboot.socket.utils.pool.memory.MemoryPool;

import java.io.IOException;

/**
 * hello world 客户端
 *
 * @author MDong
 */
public class Client {

    public static void main(String[] args) throws IOException {

        Packet demoPacket = new StringPacket("hello aio-socket");

		ClientBootstrap bootstrap = new ClientBootstrap("localhost", 8888, new ClientHandler());

		bootstrap.setBufferFactory(2 * 1024 * 1024, 2, true)
				.setReadBufferSize(1024 * 1024)
				.setWriteBufferSize(1024 * 1024, 512);

		ChannelContext start = bootstrap.start();

		Aio.send(start, demoPacket);

    }

}
