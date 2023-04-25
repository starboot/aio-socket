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
package cn.starboot.socket.plugins;

import cn.starboot.socket.Packet;
import cn.starboot.socket.core.Aio;
import cn.starboot.socket.core.AioConfig;
import cn.starboot.socket.core.ChannelContext;

import java.util.Objects;

/**
 * 集群插件
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class ClusterPlugin extends AbstractPlugin {

    public ClusterPlugin() {
        System.out.println("aio-socket version: " + AioConfig.VERSION + "; server kernel's cluster plugin added successfully");
    }

    @Override
    public boolean beforeProcess(ChannelContext channelContext, Packet packet) {
        if (!channelContext.getAioConfig().isServer()) {
            return true;
        }
        AioConfig config = channelContext.getAioConfig();
//        if (Objects.nonNull(packet.getEntity())) {
            // 是否新机器绑定到现有集群机器中、将其他机器的用户告知别的集群机器
//            if (packet.getEntity().isAuth()) {
                // 绑定到集群组
//                config.getGroups().join("ClusterServer", channelContext);
                // 绑定集群ID
//                config.getIds().join(packet.getFromId(), channelContext);
                // 设置通道ID
                channelContext.setId(packet.getFromId());
//            }else {
                // 该消息为绑定消息，将用户与指定集群中的机器进行绑定
//                config.getClusterIds().join(packet.getToId(), channelContext.getId());
//            }
//            return false;
//        }
        // 获取接收方所在ServerIP
//        String s = config.getClusterIds().get(packet.getToId());
//        if (Objects.isNull(s)) {
//            Aio.bindID(packet.getToId(), channelContext);
//            return true;
//        }
        // 判断接收方是否在本服务器
//        if (s.equals(config.getHost())) {
            // 执行处理逻辑
//            return true;
//        }
        // 否则发送到集群服务器
//        Aio.send(config.getIds().get(s), packet);
        // 本服务器不做处理逻辑
        return false;
    }


}
