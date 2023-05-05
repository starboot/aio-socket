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
import cn.starboot.socket.plugins.ACKPlugin;
import cn.starboot.socket.plugins.HeartPlugin;
import cn.starboot.socket.plugins.StreamMonitorPlugin;
import cn.starboot.socket.utils.pool.memory.MemoryPool;
import cn.starboot.socket.core.ServerBootstrap;
import cn.starboot.socket.plugins.MonitorPlugin;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 性能：870万/秒 232MB/sec
 * ------------------------------------------------
 * 			aio-socket performance
 * -------------------in 5 sec --------------------
 * inflow:				831.9873046875(MB)
 * outflow:			    829.9357376098633(MB)
 * process fail:		0
 * process count:		43512534
 * process total:		151447973
 * read count:			832
 * write count:		    212800
 * connect count:		0
 * disconnect count:	0
 * online count:		10
 * connected total:	    10
 * Requests/sec:		8702506.8
 * Transfer/sec:		166.3974609375(MB)
 * ------------------------------------------------
 *
 * ******************流量篇************************
 * ------------------------------------------------
 * 			aio-socket performance
 * -------------------in 5 sec --------------------
 * inflow:				1160.8688230514526(MB)
 * outflow:			1157.9078330993652(MB)
 * process fail:		0
 * process count:		6545725
 * process total:		50531330
 * read count:			1161
 * write count:		296859
 * connect count:		0
 * disconnect count:	0
 * online count:		10
 * connected total:	10
 * Requests/sec:		1309145.0
 * Transfer/sec:		232.17376461029053(MB)
 * ------------------------------------------------
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
//                .addPlugin(new HeartPlugin(30, 20, TimeUnit.SECONDS) {
//                    @Override
//                    public boolean isHeartMessage(Packet packet) {
//                        if (packet instanceof StringPacket) {
//							StringPacket packet1 = (StringPacket) packet;
//                            return packet1.getData().equals("heartbeat message");
//                        }
//                        return false;
//                    }
//                })
//                .addPlugin(new ACKPlugin(30, 10, TimeUnit.SECONDS, (context, lastTime) -> System.out.println("超时了：..." + lastTime)))
                .start();

//		MaintainManager maintainManager = bootstrap.getConfig().getMaintainManager();
//		Map<MaintainEnum, AbstractMaintain> map = maintainManager.getHandlerMap();
//		System.out.println(map.size());

    }
}
