package io.github.mxd888.socket.cluster;

import io.github.mxd888.socket.core.Aio;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.core.AioConfig;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.core.ClientBootstrap;

import java.io.IOException;
import java.util.Arrays;

/**
 * 集群服务启动器
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class ClusterBootstrap {

    /**
     * 集群服务器IP+port
     */
    private final String[] hosts;

    /**
     * 配置信息
     */
    private final AioConfig config;

    public ClusterBootstrap(String[] hosts, AioConfig config) {
        this.hosts = hosts;
        this.config = config;
    }

    public void start() throws IOException {
        for (String s: hosts
             ) {
            String[] split = s.split(":");
            System.out.println(Arrays.toString(split));
            ClientBootstrap clientBootstrap = new ClientBootstrap(split[0], Integer.parseInt(split[1]), config.getHandler());
            ChannelContext start = clientBootstrap.start();
            Packet packet = new Packet();
            // 写入自己的IP
            packet.setFromId(this.config.getHost());
            // 设置内核集群消息标识位
            packet.setEntity(new ClusterEntity(true));
            // 向其他服务器注册自己
            Aio.send(start, packet);
            // 绑定通道ID
            start.setId(split[0]);
            // 绑定到集群服务器组
            this.config.getGroups().join("ClusterServer", start);
            // 将通道与ID对应关系进行绑定
            this.config.getIds().join(start.getId(), start);
        }

    }
}
