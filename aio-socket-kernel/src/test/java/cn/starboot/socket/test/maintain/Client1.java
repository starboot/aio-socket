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

import cn.starboot.socket.core.Aio;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.tcp.ClientBootstrap;
import cn.starboot.socket.test.core.DemoPacket;

import java.io.IOException;

public class Client1 {

    public static void main(String[] args) throws IOException, InterruptedException {

        DemoPacket demoPacket = new DemoPacket("hello aio-socket");
        demoPacket.setFromId("999");
        demoPacket.setToId("111");
        ClientBootstrap bootstrap = new ClientBootstrap("localhost", 8888, new ClientHandler());
        ChannelContext channelContext = bootstrap.setBufferFactory(1024 * 1024, 1, true)
                .setReadBufferSize(1024 * 2)
                .setWriteBufferSize(1024 * 2, 512)
                .start();
        // 发送消息进行绑定
        Thread.sleep(1000);
        Aio.send(channelContext, demoPacket);
        Thread.sleep(5000);
        // 发送群组消息
        demoPacket.setToId("888");
        demoPacket.setData("这是发向个人的消息");
        Aio.send(channelContext, demoPacket);
        Thread.sleep(5000);
        // 关机
        bootstrap.shutdown();
    }
}
