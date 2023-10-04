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
package cn.starboot.socket.demo.outBatch.server;

import cn.starboot.socket.core.ServerBootstrap;
import cn.starboot.socket.plugins.MonitorPlugin;

/**
 * 性能：
 * ------------------------------------------------
 * 			aio-socket performance
 * -------------------in 5 sec --------------------
 * inflow:				2657.0466499328613(MB)
 * outflow:			    2656.9602966308594(MB)
 * process fail:		0
 * process count:		30280287
 * process total:		102196866
 * read count:			21403
 * write count:		    690394
 * connect count:		0
 * disconnect count:	0
 * online count:		10
 * connected total:	    10
 * Requests/sec:		6056057.4
 * Transfer/sec:		531.4093299865723(MB)
 * ------------------------------------------------
 */
public class BatchServer {

	/**
	 * 内存池改为只能使用直接内存
	 */
    public static void main(String[] args) {

		System.out.println("IP: " + args[0] + ", Port: " + args[1] + " \r\n");
        ServerBootstrap bootstrap = new ServerBootstrap(args[0], Integer.parseInt(args[1]), new ServerHandler());
        bootstrap
				.setMemoryPoolFactory(16 * 1024 * 1024, 10, true)
                .setReadBufferSize(1024 * 1024)
                .setWriteBufferSize(1024 * 4, 512)
                .addPlugin(new MonitorPlugin(5))
				.setThreadNum(Runtime.getRuntime().availableProcessors())
                .start();
    }
}
