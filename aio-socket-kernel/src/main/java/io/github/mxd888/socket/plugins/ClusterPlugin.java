package io.github.mxd888.socket.plugins;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.core.Aio;
import io.github.mxd888.socket.core.AioConfig;
import io.github.mxd888.socket.core.TCPChannelContext;

import java.util.Objects;

/**
 * 集群插件
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class ClusterPlugin extends AbstractPlugin {

    public ClusterPlugin() {
        System.out.println("aio-socket "+"version: " + AioConfig.VERSION + "; server kernel's cluster plugin added successfully");
    }

    @Override
    public boolean beforeProcess(TCPChannelContext channelContext, Packet packet) {
        if (!channelContext.getAioConfig().isServer()) {
            return true;
        }
        AioConfig config = channelContext.getAioConfig();
        if (Objects.nonNull(packet.getEntity())) {
            // 是否新机器绑定到现有集群机器中、将其他机器的用户告知别的集群机器
            if (packet.getEntity().isAuth()) {
                // 绑定到集群组
                config.getGroups().join("ClusterServer", channelContext);
                // 绑定集群ID
                config.getIds().join(packet.getFromId(), channelContext);
                // 设置通道ID
                channelContext.setId(packet.getFromId());
            }else {
                // 该消息为绑定消息，将用户与指定集群中的机器进行绑定
                config.getClusterIds().join(packet.getToId(), channelContext.getId());
            }
            return false;
        }
        // 获取接收方所在ServerIP
        String s = config.getClusterIds().get(packet.getToId());
        if (Objects.isNull(s)) {
            Aio.bindID(packet.getToId(), channelContext);
            return true;
        }
        // 判断接收方是否在本服务器
        if (s.equals(config.getHost())) {
            // 执行处理逻辑
            return true;
        }
        // 否则发送到集群服务器
        Aio.send(config.getIds().get(s), packet);
        // 本服务器不做处理逻辑
        return false;
    }


}
