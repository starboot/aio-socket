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

import cn.starboot.socket.Packet;
import cn.starboot.socket.core.Aio;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.test.core.DemoPacket;

public class ServerHandler extends DemoHandler {
    @Override
    public Packet handle(ChannelContext channelContext, Packet packet) {


        if (packet instanceof DemoPacket) {
            DemoPacket packet1 = (DemoPacket) packet;
            System.out.println("收到消息：" + packet1.getFromId() + "-" + packet1.getToId() + "-" + packet1.getData());
            if (channelContext.getId() == null) {
				boolean b1 = Aio.bindId(packet.getFromId(), channelContext);
				boolean b = Aio.bindGroup("1191998028", channelContext);
				System.out.println("进行绑定");
            }
            if (packet.getToId() != null && !packet.getToId().equals("111")) {

                if (packet.getToId().equals("123")) {
                    System.out.println("群发");
                    Aio.sendGroup("1191998028", packet, channelContext, null, false);
                    return null;
                }
                System.out.println("私发");
                Aio.sendToId(packet.getToId(), packet, channelContext.getAioConfig());
                return null;
            }
            packet1.setData("服务器收到消息：" + packet1.getData());
            return packet1;
        }
        return null;
    }

    private boolean Test(ChannelContext channelContext) {
    	return true;
//		return Aio.removeUserFromGroup(channelContext, "") && Aio.removeUserFromAllGroup(channelContext);
	}
}
