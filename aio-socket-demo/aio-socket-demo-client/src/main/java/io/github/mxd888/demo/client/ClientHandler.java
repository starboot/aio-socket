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
package io.github.mxd888.demo.client;

import io.github.mxd888.demo.common.DemoPacket;
import io.github.mxd888.demo.common.Handler;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.core.TCPChannelContext;


public class ClientHandler extends Handler {

    private long count = 0L;

    @Override
    public Packet handle(ChannelContext channelContext, Packet packet) {
        DemoPacket packet1 = (DemoPacket) packet;
        if (!packet1.getData().equals("hello aio-socket")) {
            System.out.println("不一致，出错啦:" + packet1.getData());
        }else {
            count++;
            if (count % 1000000 ==0) {
                System.out.println("已收到" + (count / 1000000) + "百万条消息");
            }
        }
        return null;
    }
}
