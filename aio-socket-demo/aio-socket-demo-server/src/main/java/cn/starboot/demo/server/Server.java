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
package cn.starboot.demo.server;

import cn.starboot.demo.common.TestPacket;
import cn.starboot.socket.Packet;
import cn.starboot.socket.codec.string.StringPacket;
import cn.starboot.socket.maintain.AbstractMaintain;
import cn.starboot.socket.maintain.MaintainEnum;
import cn.starboot.socket.maintain.MaintainManager;
import cn.starboot.socket.plugins.HeartPlugin;
import cn.starboot.socket.plugins.StreamMonitorPlugin;
import cn.starboot.socket.utils.pool.memory.MemoryPool;
import cn.starboot.socket.core.ServerBootstrap;
import cn.starboot.socket.plugins.MonitorPlugin;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *  -----5seconds ----
 *  inflow:		771.9616775512695(MB)
 *  outflow:	773.6727828979492(MB)
 *  process fail:	0
 *  process count:	40514816
 *  process total:	82435765
 *  read count:	1038	write count:	198011
 *  connect count:	0
 *  disconnect count:	0
 *  online count:	10
 *  connected total:	10
 *  Requests/sec:	8102963.2
 *  Transfer/sec:	154.3923355102539(MB)
 */
public class Server {

	/**
	 * 内存池改为只能使用直接内存
	 */
    public static void main(String[] args) {

        ServerBootstrap bootstrap = new ServerBootstrap("localhost", 8888, new ServerHandler());
        bootstrap.setMemoryPoolFactory(() -> new MemoryPool(10 * 1024 * 1024, 10, true))
                .setReadBufferSize(1024 * 1024)
                .setWriteBufferSize(1024 * 4, 512)
                // 注册流量监控插件
//                .addPlugin(new StreamMonitorPlugin())
                .addPlugin(new MonitorPlugin(5))
				.addAioHandler(new MyServerHandler(5))
//                .addPlugin(new HeartPlugin(30, TimeUnit.SECONDS) {
//                    @Override
//                    public boolean isHeartMessage(Packet packet) {
//                        if (packet instanceof StringPacket) {
//							StringPacket packet1 = (StringPacket) packet;
//                            return packet1.getData().equals("heartbeat message");
//                        }
//                        return false;
//                    }
//                })
//                .addPlugin(new ACKPlugin(30, TimeUnit.SECONDS, (context, lastTime) -> System.out.println("超时了：..." + lastTime)))
                .start();

//		MaintainManager maintainManager = bootstrap.getConfig().getMaintainManager();
//		Map<MaintainEnum, AbstractMaintain> map = maintainManager.getHandlerMap();
//		System.out.println(map.size());

    }
}
