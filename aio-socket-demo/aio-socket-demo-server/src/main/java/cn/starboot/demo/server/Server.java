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
 * 性能：
 * 消息处理量：1158万/秒
 * 流量流动量：270MB/sec (input, output)
 * ------------------------------------------------
 * 			aio-socket performance
 * -------------------in 5 sec --------------------
 * inflow:				1104.983139038086(MB)
 * outflow:			    1104.6816596984863(MB)
 * process fail:		0
 * process count:		57917126
 * process total:		732974447
 * read count:			1105
 * write count:		    283043
 * connect count:		0
 * disconnect count:	0
 * online count:		10
 * connected total:	    10
 * Requests/sec:		1.15834252E7
 * Transfer/sec:		220.9966278076172(MB)
 * ------------------------------------------------
 *
 * ***********************流量篇*****************************
 * ------------------------------------------------
 * 			aio-socket performance
 * -------------------in 5 sec --------------------
 * inflow:				1355.9301261901855(MB)
 * outflow:			    1356.981243133545(MB)
 * process fail:		0
 * process count:		15455294
 * process total:		48235737
 * read count:			1355
 * write count:		    298008
 * connect count:		0
 * disconnect count:	0
 * online count:		10
 * connected total:	    10
 * Requests/sec:		3091058.8
 * Transfer/sec:		271.18602523803713(MB)
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
//				.addAioHandler(new MyServerHandler(5))
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
