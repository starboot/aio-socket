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
package cn.starboot.socket.test.maintain;

import cn.starboot.socket.core.ServerBootstrap;

public class Server {

    public static void main(String[] args) {

		ServerBootstrap
				.startTCPService()
				.listen("localhost", 8888)
				.addAioHandler(new ServerHandler())
				.setMemoryPoolFactory(16 * 1024 * 1024, 10, true)
				.setReadBufferSize(1024 * 1024)
				.setWriteBufferSize(1024 * 4, 512)
				.setMemoryKeep(true)
				.setThreadNum(Runtime.getRuntime().availableProcessors())
				.start();
    }
}
