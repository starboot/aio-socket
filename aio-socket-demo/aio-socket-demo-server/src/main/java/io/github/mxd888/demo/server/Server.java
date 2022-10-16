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
package io.github.mxd888.demo.server;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.utils.pool.memory.MemoryPool;
import io.github.mxd888.socket.core.ServerBootstrap;
import io.github.mxd888.socket.plugins.ACKPlugin;
import io.github.mxd888.socket.plugins.HeartPlugin;
import io.github.mxd888.socket.plugins.MonitorPlugin;

import java.util.concurrent.TimeUnit;

/**
 * -----5seconds ----
 * inflow:		751.988525390625(MB)
 * outflow:	    751.5911026000977(MB)
 * process fail:	0
 * process count:	39405082
 * process total:	156391430
 * read count:	752	write count:	192410
 * connect count:	0
 * disconnect count:	0
 * online count:	10
 * connected total:	10
 * Requests/sec:	7881016.4
 * Transfer/sec:	150.397705078125(MB)
 */
public class Server {

    public static void main(String[] args) {

        ServerBootstrap bootstrap = new ServerBootstrap("localhost", 8888, new ServerHandler());
        bootstrap.setMemoryPoolFactory(() -> new MemoryPool(10 * 1024 * 1024, 10, true))
                .setReadBufferSize(1024 * 1024)
                .setWriteBufferSize(1024 * 4, 512)
                // 注册流量监控插件
//                .addPlugin(new StreamMonitorPlugin())
                .addPlugin(new MonitorPlugin(5))
//                .addPlugin(new HeartPlugin(30, TimeUnit.SECONDS) {
//                    @Override
//                    public boolean isHeartMessage(Packet packet) {
//                        if (packet instanceof DemoPacket) {
//                            DemoPacket packet1 = (DemoPacket) packet;
//                            return packet1.getData().equals("heartbeat message");
//                        }
//                        return false;
//                    }
//                })
//                .addPlugin(new ACKPlugin(30, TimeUnit.SECONDS, (context, lastTime) -> System.out.println("超时了：..." + lastTime)))
                .start();

    }
}
