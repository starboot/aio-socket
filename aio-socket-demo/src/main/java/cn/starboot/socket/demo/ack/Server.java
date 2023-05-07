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

import cn.starboot.socket.core.ServerBootstrap;
import cn.starboot.socket.plugins.ACKPlugin;
import cn.starboot.socket.plugins.MonitorPlugin;
import cn.starboot.socket.utils.pool.memory.MemoryPool;

import java.util.concurrent.TimeUnit;

public class Server {

    public static void main(String[] args) {

        ServerBootstrap bootstrap = new ServerBootstrap("localhost", 8888, new ServerHandler());
        bootstrap.setMemoryPoolFactory(() -> new MemoryPool(10 * 1024 * 1024, 10, true))
                .setReadBufferSize(1024 * 1024)
                .setWriteBufferSize(1024 * 4, 512)
				.addPlugin(new MonitorPlugin(5))
				.addPlugin(new ACKPlugin(6, 2, TimeUnit.SECONDS, (packet, lastTime) -> System.out.println(packet.getReq() + " 超时了")))
                .start();

    }
}
